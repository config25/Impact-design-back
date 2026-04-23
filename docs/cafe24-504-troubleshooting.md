# cafe24 504 Gateway Timeout 트러블슈팅 가이드

## 📌 상황 요약

- **도메인**: iptdesign.mycafe24.com (172.234.87.250)
- **증상**:
  - 웹 접속 시 `504 Gateway Timeout`
  - SSH 접속 불가 (비밀번호 입력창조차 안 뜸)
  - Ping 응답 없음 (100% 손실)
  - TCP 443 연결 실패

---

## 🔍 504 Gateway Timeout이란?

```
[클라이언트] → [cafe24 nginx/proxy] → [내 Spring 앱(WAS)]
                                           ↑
                                    여기서 응답 없음
```

- **503** = cafe24 서비스 자체 불가
- **504** = cafe24 프록시가 **업스트림(내 WAS)에서 응답을 못 받음**

---

## 🧭 진단 순서

### 1단계: 외부 접속성 확인

| 테스트 | 명령 | 의미 |
|--------|------|------|
| Ping | `ping iptdesign.mycafe24.com` | ICMP 응답 |
| HTTPS 포트 | `Test-NetConnection iptdesign.mycafe24.com -Port 443` | 웹 포트 열림 여부 |
| SSH 포트 | `Test-NetConnection iptdesign.mycafe24.com -Port 22` | SSH 포트 열림 여부 |

**실제 결과 (현재 상황)**:
```
PingSucceeded    : False
TcpTestSucceeded : False
```
→ 호스트 자체가 네트워크 응답 불능

### 2단계: 다른 네트워크에서 확인

핸드폰 테더링 등 **다른 IP**에서 동일 테스트:
- 테더링에선 접속됨 → **내 IP가 차단됨** (fail2ban 등)
- 테더링에서도 실패 → **서버/인프라 장애**

### 3단계: cafe24 측 상태 확인

- [cafe24 관리 콘솔](https://hosting.cafe24.com) 로그인
- 공지사항: https://hosting.cafe24.com/?controller=notice_list
- 서비스 상태 / 재시작 버튼 확인

---

## ❗ 원인 가능성 (가능성 높은 순)

| # | 원인 | 내 책임 | 특징 |
|---|------|:------:|------|
| 1 | **cafe24 호스트/네트워크 장애** | ❌ | ping/SSH 모두 불가, 공지 있을 수 있음 |
| 2 | **cafe24 측 IP 차단** | ❌ | 다른 네트워크에선 접속됨 |
| 3 | **OS 커널 패닉** | ❌ | 드뭄, 재부팅만이 해법 |
| 4 | **앱 OOM 연쇄 피해** | ⚠️ 간접 | 일반적으론 OS가 살아남아 SSH는 됨 |
| 5 | **디스크 full** | ⚠️ 간접 | syslog/sshd 실패 가능 |

### ❌ 앱 코드 자체가 직접 원인일 가능성은 낮음

- Java OOM → JVM만 죽음, OS는 멀쩡 → SSH 정상
- Linux OOM Killer → 오히려 OS를 살리려고 Java 죽임 → SSH 정상
- **NPE 등 코드 버그가 OS 네트워크를 먹통시키지 않음**

따라서 **ping/SSH/TCP 모두 실패 = OS/호스트 레벨 문제** → cafe24 이슈일 확률 높음

---

## 🚨 즉시 조치

### 1. cafe24 관리 콘솔 접속
```
https://hosting.cafe24.com
→ 로그인 → 나의 서비스 관리 → 서버 상태
```
- "재시작" 버튼 있으면 클릭 → 5~10분 대기

### 2. 콘솔 안 되면 전화
```
cafe24 고객센터: 1588-3284
운영시간: 평일 09:00~18:00
```

**전달 템플릿**:
```
도메인: iptdesign.mycafe24.com
IP: 172.234.87.250
증상:
 - 웹 504 Gateway Timeout
 - SSH 접속 불가 (핸드셰이크 실패)
 - Ping 응답 없음
 - TCP 443 연결 실패

요청: 서버 상태 확인 및 강제 재부팅 부탁드립니다.
```

### 3. 업무시간 외 → 1:1 문의
```
https://hosting.cafe24.com → 고객센터 → 1:1 문의
```

---

## 🔬 서버 복구 후 원인 분석

SSH 접속 가능해지면 **즉시** 로그 확보:

```bash
# OOM 킬러 흔적
dmesg | grep -i "killed process"
sudo journalctl -k --since "2 hours ago" | grep -iE "oom|memory|killed"

# 디스크 사용량
df -h

# 부팅 기록 (다운된 시점 역산)
last -x | head -20
uptime

# Spring 앱 마지막 로그
tail -500 /경로/logs/application.log

# HikariCP / DB 연결 에러 흔적
grep -iE "hikari|connection is not available|deadlock" /경로/logs/*.log
```

### 판독법

| 로그 결과 | 결론 |
|----------|------|
| `Killed process XXX (java)` | 앱 메모리 문제 (간접) |
| 디스크 100% | 로그 폭증 (간접) |
| 부팅 시간이 cafe24 점검과 일치 | cafe24 인프라 문제 |
| 로그 없이 갑자기 부팅됨 | 호스트 강제 종료 (cafe24 측) |

---

## 🛡 재발 방지 체크리스트

### JVM / 메모리 (1GB 서버)
- [ ] `-Xmx` 를 보수적으로 (400~512MB)
- [ ] `-XX:+HeapDumpOnOutOfMemoryError` 설정
- [ ] 스왑 메모리 2GB 이상 (cafe24 플랜 허용 시)

### Connection Pool
- [ ] HikariCP `maximum-pool-size` 5~10
- [ ] Tomcat `server.tomcat.max-threads` 50 이하

### 로그
- [ ] logrotate 설정 (디스크 full 방지)
- [ ] DEBUG 로그 프로덕션 비활성화

### 모니터링
- [ ] 외부 Uptime 감시 (Uptime Kuma, UptimeRobot 등 무료)
- [ ] 이상 시 알림 (이메일/슬랙)

### 인프라
- [ ] 트래픽이 늘고 있으면 **2GB+ 플랜 업그레이드** 검토
- [ ] 정기 백업 상태 확인

---

## 📎 핵심 교훈

1. **504 + SSH 불가 + Ping 불가** = 호스트 레벨 문제 (앱 코드 탓 아님)
2. **앱 OOM은 OS를 죽이지 않는다** — OOM Killer가 오히려 OS를 보호함
3. **외부에서 호스트 접근 불가 시** 여러분이 할 수 있는 건 없음 → cafe24 문의
4. **재발 방지**는 서버 복구 후 로그 분석부터
