-- ============================================
-- seed_data_3.sql - win_canvas 및 하위 테이블
-- seed_data_2.sql 실행 후 이 파일 실행
-- ============================================

-- win_canvas (120 rows) - canvas_id = (user_id-10)*2+1(QUICK), +2(BUILD)
INSERT INTO win_canvas (canvas_id, canvas_type, crisis_signal, pain_touch_point, strategic_goal, task_description, task_name, user_id, submitted) VALUES
(1,'QUICK','경쟁사 대비 인지도 하락 추세','브랜드 메시지 일관성 부족','브랜드 인지도 긴급 조사','5개 주요 시장 브랜드 인지도 현황 파악 및 개선 방향 도출','글로벌 브랜드 인지도 서베이',10,1),
(2,'BUILD','브랜드 포트폴리오 혼란','멀티브랜드 시너지 부족','글로벌 브랜드 플랫폼 구축','현대·제네시스·아이오닉 브랜드 체계 재정립 및 통합 관리','통합 브랜드 아키텍처 설계',10,1),
(3,'QUICK','SNS 팔로워 성장률 정체','콘텐츠 현지화 부족','SNS 글로벌 캠페인 기획','인스타그램·유튜브 중심 글로벌 통합 브랜드 캠페인 기획','소셜미디어 통합 캠페인 실행',11,1),
(4,'BUILD','전기차 브랜드 경쟁 심화','ICE→EV 브랜드 전환 전략 부재','전기차 시대 브랜드 전략','전기차 전환 시대에 맞는 현대차 브랜드 재포지셔닝','EV 브랜드 포지셔닝 전략',11,1),
(5,'QUICK','스폰서십 비용 대비 효과 불명확','효과 측정 체계 미비','스포츠 스폰서십 효과 분석','글로벌 스포츠 스폰서십 투자 대비 효과 측정','FIFA 스폰서십 ROI 분석',12,1),
(6,'BUILD','프리미엄 세그먼트 경쟁','브랜드 프리미엄 인식 부족','프리미엄 브랜드 강화','대중 브랜드에서 프리미엄 브랜드로의 인식 전환 전략','현대차 프리미엄화 로드맵',12,1),
(7,'QUICK','광고 효율 지역별 편차 큼','크리에이티브 가이드라인 부재','브랜드 캠페인 A/B 테스트','주요 시장별 광고 크리에이티브 효과 비교 분석','글로벌 광고 소재 테스트',13,1),
(8,'BUILD','캠페인 단발성 운영','장기 브랜드 빌딩 부재','글로벌 브랜드 캠페인 체계','연중 상시 운영되는 글로벌 통합 브랜드 캠페인 체계 구축','Always-on 브랜드 캠페인 설계',13,1),
(9,'QUICK','경쟁사 브랜드 가치 급상승','체계적 경쟁 분석 부재','경쟁사 브랜드 벤치마킹','주요 경쟁사 브랜드 포지셔닝 및 캠페인 분석','토요타·BYD 브랜드 전략 분석',14,1),
(10,'BUILD','오프라인 브랜드 접점 부족','브랜드 경험 공간 부재','브랜드 경험 생태계 구축','글로벌 주요 도시 현대 브랜드 경험관 설립 및 운영','오프라인 브랜드 스페이스 전략',14,1),
(11,'QUICK','브랜드 일관성 저하','기존 가이드라인 노후화','브랜드 가이드라인 업데이트','전기차 시대에 맞는 브랜드 비주얼 아이덴티티 업데이트','글로벌 브랜드 매뉴얼 개정',15,1),
(12,'BUILD','그린워싱 리스크 증가','ESG 브랜드 전략 부재','지속가능 브랜드 전략','지속가능경영을 브랜드 핵심 가치로 통합하는 장기 전략','ESG 브랜드 가치 체계 구축',15,1),
(13,'QUICK','젊은 층 브랜드 호감도 하락','인플루언서 관리 체계 미흡','인플루언서 브랜드 협업','MZ세대 타겟 인플루언서 협업 캠페인 기획 및 실행','글로벌 인플루언서 파트너십',16,1),
(14,'BUILD','젊은 층 브랜드 이탈','세대별 전략 미분화','MZ세대 브랜드 전략','MZ세대와의 장기적 브랜드 관계 구축을 위한 전략 수립','차세대 고객 브랜드 관계 구축',16,1),
(15,'QUICK','ESG 워싱 리스크','ESG 메시지 전달력 약함','ESG 브랜드 커뮤니케이션','ESG 활동을 브랜드 가치와 연계한 커뮤니케이션 강화','지속가능경영 브랜드 스토리',17,1),
(16,'BUILD','브랜드 관리 비일관성','글로벌-로컬 갈등','글로벌 브랜드 거버넌스','글로벌 일관성과 로컬 유연성을 양립하는 거버넌스 구축','브랜드 관리 체계 글로벌화',17,1),
(17,'QUICK','전시회 방문객 감소 추세','디지털 전시 경험 부족','모터쇼 브랜드 경험 혁신','주요 전시회에서의 브랜드 경험 설계 및 실행','CES·모터쇼 부스 전략 수립',18,1),
(18,'BUILD','모빌리티 패러다임 전환','자동차 중심 브랜드 한계','모빌리티 브랜드 확장','자동차를 넘어 모빌리티 솔루션 브랜드로의 확장 전략','미래 모빌리티 브랜드 전략',18,1),
(19,'QUICK','소셜미디어 위기 확산 속도','위기 대응 프로세스 미정립','브랜드 위기관리 매뉴얼','브랜드 이슈 발생 시 신속 대응을 위한 매뉴얼 수립','글로벌 브랜드 위기 대응 체계',19,1),
(20,'BUILD','브랜드 투자 ROI 불명확','브랜드 가치 측정 미흡','브랜드 가치 측정 체계','브랜드 가치를 정량적으로 측정하고 관리하는 체계 구축','글로벌 브랜드 밸류에이션 시스템',19,1),
(21,'QUICK','CPC 비용 지속 상승','키워드 전략 비효율','구글 광고 캠페인 최적화','구글 검색·디스플레이 광고 캠페인 성과 최적화','SEM 캠페인 효율 개선',20,1),
(22,'BUILD','고객 데이터 분산','데이터 통합 플랫폼 부재','CDP 플랫폼 구축','전사 고객 데이터 통합 CDP 플랫폼 구축 및 활용 체계 수립','고객 데이터 플랫폼 도입',20,1),
(23,'QUICK','콘텐츠 생산 속도 부족','채널별 전략 불명확','소셜미디어 콘텐츠 캘린더','인스타·유튜브·틱톡 채널별 콘텐츠 기획 및 일정 관리','월간 SNS 콘텐츠 플랜 수립',21,1),
(24,'BUILD','개인화 수준 경쟁 열위','AI 마케팅 역량 부족','AI 개인화 마케팅 시스템','AI를 활용한 고객별 맞춤형 콘텐츠·오퍼 자동 생성 시스템','AI 기반 1:1 마케팅',21,1),
(25,'QUICK','이메일 오픈율 하락','개인화 수준 낮음','이메일 자동화 캠페인','고객 세그먼트별 자동화 이메일 시나리오 설계','마케팅 이메일 시퀀스 구축',22,1),
(26,'BUILD','채널 간 단절','옴니채널 인프라 미구축','옴니채널 통합 플랫폼','온라인-오프라인-모바일 채널 간 끊김 없는 고객 경험 구축','온오프라인 채널 통합',22,1),
(27,'QUICK','웹사이트 이탈률 높음','모바일 UX 미흡','랜딩페이지 전환율 개선','차량 모델별 랜딩페이지 UX 개선 및 전환율 최적화','웹사이트 CRO 프로젝트',23,1),
(28,'BUILD','레거시 시스템 한계','마테크 스택 파편화','마테크 스택 현대화','마케팅 자동화·분석·CRM 등 마테크 스택 통합 현대화','마케팅 기술 스택 재구축',23,1),
(29,'QUICK','구독자 성장률 둔화','영상 조회수 경쟁 심화','유튜브 채널 성장 전략','차량 리뷰·라이프스타일 영상 콘텐츠 기획','브랜드 유튜브 콘텐츠 강화',24,1),
(30,'BUILD','온라인 구매 트렌드 가속','디지털 구매 경험 미흡','디지털 커머스 플랫폼','온라인 차량 구매 전 과정을 지원하는 이커머스 플랫폼 구축','온라인 차량 구매 플랫폼',24,1),
(31,'QUICK','광고 피로도 증가','쿠키 규제 영향','리타게팅 광고 고도화','웹사이트 방문자 대상 리타게팅 광고 세분화','디스플레이 리타게팅 최적화',25,1),
(32,'BUILD','개인정보 규제 강화','데이터 거버넌스 부재','데이터 거버넌스 체계','개인정보보호 규제 준수 및 데이터 품질 관리 체계 구축','마케팅 데이터 관리 체계',25,1),
(33,'QUICK','앱 삭제율 증가','푸시 수신 거부율 상승','모바일 앱 푸시 전략','현대 앱 푸시 알림 시나리오 및 빈도 최적화','앱 사용자 리텐션 개선',26,1),
(34,'BUILD','수동 미디어 바잉 비효율','프로그래매틱 역량 부족','프로그래매틱 광고 고도화','프로그래매틱 미디어 바잉 및 실시간 최적화 시스템 구축','자동화 광고 시스템 구축',26,1),
(35,'QUICK','온라인 기사 노출 감소','디지털 PR 전문 인력 부족','디지털 PR 캠페인','주요 온라인 미디어·블로그 대상 디지털 PR 실행','온라인 미디어 홍보 강화',27,1),
(36,'BUILD','콘텐츠 수요 폭증','콘텐츠 생산 병목','콘텐츠 팩토리 구축','AI 활용 대량 콘텐츠 생산 및 배포 자동화 체계 구축','대량 콘텐츠 생산 체계',27,1),
(37,'QUICK','고객 문의 응답 지연','24시간 응대 불가','챗봇 고객 상담 도입','웹사이트·카카오톡 AI 챗봇 고객 상담 도입','AI 챗봇 시범 운영',28,1),
(38,'BUILD','디지털 역량 분산','전문 조직 부재','글로벌 디지털 허브','글로벌 디지털 마케팅 역량을 집중하는 CoE 조직 구축','디지털 마케팅 CoE 설립',28,1),
(39,'QUICK','부정 여론 감지 지연','모니터링 수동 운영','소셜 리스닝 대시보드','소셜미디어 브랜드 언급·감성 분석 실시간 모니터링','브랜드 언급 모니터링 구축',29,1),
(40,'BUILD','온프레미스 시스템 한계','클라우드 전환 지연','마케팅 클라우드 전환','마케팅 시스템 전체를 클라우드 기반으로 전환','클라우드 기반 마케팅 인프라',29,1),
(41,'QUICK','고객 불만 접수 증가','VOC 분석 수작업 의존','VOC 분석 대시보드 구축','고객 불만·건의사항 실시간 분석 대시보드 구축','고객 불만 분석 시스템',30,1),
(42,'BUILD','고객 경험 단절','통합 CX 전략 부재','프리미엄 CX 생태계 구축','인지-구매-사용-재구매 전 생애주기 프리미엄 CX 설계','통합 고객 경험 플랫폼',30,1),
(43,'QUICK','시승 예약 이탈률 높음','예약 프로세스 복잡','시승 예약 UX 개선','시승 예약 웹·앱 인터페이스 개선 및 간소화','온라인 시승 예약 프로세스',31,1),
(44,'BUILD','고객 가치 평가 미흡','CLV 모델 부재','고객 생애가치 관리 체계','고객 생애가치 예측 및 가치 기반 마케팅 자원 배분 체계','CLV 기반 마케팅 체계',31,1),
(45,'QUICK','딜러별 서비스 품질 편차','매뉴얼 최신화 미흡','고객 응대 매뉴얼 표준화','전국 딜러 고객 응대 표준 매뉴얼 제작 및 배포','딜러 서비스 매뉴얼 개정',32,1),
(46,'BUILD','딜러 경험 경쟁 열위','딜러십 디지털화 지연','딜러 경험 혁신 프로그램','전국 딜러십의 디지털 전환 및 경험 혁신 프로그램','스마트 딜러십 전환',32,1),
(47,'QUICK','NPS 점수 정체','조사 방법론 비표준화','NPS 조사 체계 구축','분기별 NPS 조사 설계 및 결과 분석 체계 수립','고객 추천지수 정기 측정',33,1),
(48,'BUILD','구독 경제 확산','소유 모델 한계','구독형 모빌리티 서비스','월정액 차량 구독·교환 서비스 설계 및 마케팅','차량 구독 서비스 설계',33,1),
(49,'QUICK','구매 후 만족도 하락','팔로업 체계 부재','구매 후 팔로업 프로그램','차량 출고 후 7일·30일·90일 팔로업 프로세스 설계','출고 후 고객 케어 강화',34,1),
(50,'BUILD','고객 충성도 하락','로열티 프로그램 분산','고객 충성도 프로그램','현대차 전 브랜드 통합 멤버십·리워드 프로그램 구축','통합 로열티 프로그램 구축',34,1),
(51,'QUICK','커뮤니티 참여율 저조','커뮤니티 관리 인력 부족','고객 커뮤니티 활성화','차종별 온라인 오너 커뮤니티 활성화 방안 수립','온라인 오너 커뮤니티 운영',35,1),
(52,'BUILD','고객 서비스 비용 증가','서비스 자동화 수준 낮음','AI 고객 서비스 혁신','AI 챗봇·음성 어시스턴트 등 지능형 고객 서비스 구축','AI 기반 고객 서비스 플랫폼',35,1),
(53,'QUICK','AS 예약 대기 불만','예약 시스템 불편','서비스센터 예약 개선','서비스센터 온라인 예약 시스템 사용성 개선','AS 예약 시스템 UX 개선',36,1),
(54,'BUILD','고객 데이터 파편화','통합 고객 뷰 부재','고객 데이터 360도 뷰','모든 고객 접점 데이터를 통합한 360도 고객 뷰 구축','통합 고객 프로필 시스템',36,1),
(55,'QUICK','VIP 고객 이탈 징후','고객 감사 프로그램 부족','고객 감사 이벤트 기획','충성 고객 대상 특별 시승·이벤트 프로그램 기획','VIP 고객 감사 행사',37,1),
(56,'BUILD','서비스 혁신 정체','디자인 씽킹 역량 부족','서비스 디자인 혁신','서비스 디자인 방법론 도입으로 고객 중심 혁신 체계화','CX 디자인 씽킹 도입',37,1),
(57,'QUICK','딜러 서비스 평가 점수 하락','교육 체계 미흡','딜러 교육 프로그램','딜러 직원 대상 고객 경험 중심 교육 프로그램 운영','고객 응대 역량 강화 교육',38,1),
(58,'BUILD','지역별 CX 품질 편차','글로벌 표준 부재','글로벌 CX 표준 수립','전 세계 공통 CX 표준 수립 및 현지화 가이드 개발','글로벌 고객 경험 표준화',38,1),
(59,'QUICK','고객 이탈 포인트 미파악','여정 데이터 통합 부족','고객 여정 맵 업데이트','인지-구매-사용-재구매 전 과정 고객 여정 맵 업데이트','온오프라인 고객 여정 분석',39,1),
(60,'BUILD','CX 측정 비체계적','지표 간 연계 부족','고객 경험 측정 체계','NPS·CSAT·CES 등 CX 지표 통합 측정 및 관리 체계','CX 지표 통합 관리 시스템',39,1),
(61,'QUICK','출시 인지도 목표 미달 우려','론칭 리드타임 부족','아이오닉6 론칭 캠페인','아이오닉6 국내·글로벌 론칭 통합 마케팅 캠페인','신차 출시 마케팅 실행',40,1),
(62,'BUILD','EV 라인업 인지도 분산','통합 EV 마케팅 부재','전기차 라인업 통합 마케팅','아이오닉·코나EV 등 전기차 라인업 통합 마케팅 전략 수립','EV 포트폴리오 마케팅 전략',40,1),
(63,'QUICK','SUV 시장 경쟁 심화','모델 간 차별화 인식 부족','SUV 라인업 비교 콘텐츠','SUV 모델 간 비교 콘텐츠 제작으로 크로스셀링 유도','투싼·싼타페 비교 가이드',41,1),
(64,'BUILD','모델 간 카니발리제이션','포트폴리오 전략 불명확','제품 포트폴리오 재편','세단·SUV·EV·수소차 포트폴리오 전략적 재편','차종별 포지셔닝 재정립',41,1),
(65,'QUICK','전기차 불안감 여전','시승 기회 접근성 부족','전기차 시승 프로그램 확대','전국 주요 도시 전기차 시승 체험 프로그램 확대','EV 체험 마케팅 강화',42,1),
(66,'BUILD','기술 브랜드 인지도 낮음','기술 브랜딩 체계 부재','기술 브랜딩 체계 구축','E-GMP·HTWO 등 핵심 기술의 브랜드 자산화 전략','핵심 기술 브랜드화',42,1),
(67,'QUICK','가격 경쟁 심화','가격 모니터링 체계 미흡','경쟁 모델 가격 분석','BYD·테슬라 등 경쟁 모델 가격 변동 추적 및 대응','가격 경쟁력 모니터링',43,1),
(68,'BUILD','론칭 프로세스 비표준화','글로벌 협업 비효율','글로벌 론칭 프로세스 혁신','글로벌 신차 론칭을 위한 표준 플레이북 및 프로세스 구축','신차 론칭 플레이북',43,1),
(69,'QUICK','부정 리뷰 확산 리스크','리뷰 관리 수동 대응','제품 리뷰 관리 체계','자동차 전문 리뷰어 관리 및 제품 리뷰 모니터링 체계','유튜브·블로그 리뷰 대응',44,1),
(70,'BUILD','모빌리티 서비스 시장 도래','MaaS 마케팅 준비 부족','모빌리티 서비스 마케팅','자율주행·로보택시 등 미래 모빌리티 서비스 마케팅 전략','MaaS 마케팅 전략 수립',44,1),
(71,'QUICK','옵션 선택 복잡성','패키지 구성 최적화 필요','옵션 패키지 마케팅','고객 선호 옵션 조합 분석 기반 패키지 프로모션 기획','인기 옵션 번들 프로모션',45,1),
(72,'BUILD','디지털 체험 수요 증가','디지털 쇼룸 인프라 부족','제품 경험 디지털화','VR 쇼룸·AR 시승 등 디지털 제품 체험 플랫폼 구축','VR·AR 제품 체험 플랫폼',45,1),
(73,'QUICK','충전 불편 인식 해소 필요','충전 정보 접근성 부족','EV 충전 인프라 홍보','현대 전용 충전소·제휴 충전소 네트워크 홍보 캠페인','충전 네트워크 안내 캠페인',46,1),
(74,'BUILD','배터리 기술 경쟁 심화','기술 커뮤니케이션 미흡','배터리 기술 마케팅 전략','전고체 배터리 등 차세대 배터리 기술 마케팅 전략','배터리 기술 리더십 확보',46,1),
(75,'QUICK','중고차 가치 하락 우려','잔존가치 정보 부족','중고차 잔존가치 홍보','현대차 높은 잔존가치를 활용한 마케팅 캠페인','현대 인증중고차 마케팅',47,1),
(76,'BUILD','수소 인프라 부족','수소차 인식 부족','수소차 시장 개척 전략','넥쏘·상용 수소차 시장 개척을 위한 마케팅 전략','수소 모빌리티 시장 창출',47,1),
(77,'QUICK','커넥티드 기능 인지도 낮음','서비스 가치 전달 미흡','커넥티드카 서비스 홍보','블루링크·OTA 업데이트 등 커넥티드 서비스 홍보','블루링크 기능 마케팅',48,1),
(78,'BUILD','커넥티드 서비스 수익화 필요','서비스 가치 인식 부족','커넥티드 서비스 마케팅 체계','OTA 업데이트·커넥티드 서비스를 수익 모델로 발전','OTA·커넥티드 서비스 가치화',48,1),
(79,'QUICK','프로모션 효과 감소 추세','프로모션 차별화 부족','시즌별 프로모션 기획','시즌·명절 연계 프로모션 캠페인 기획 및 실행','분기별 판촉 캠페인',49,1),
(80,'BUILD','가격 경쟁 심화','가격 전략 비체계적','글로벌 가격 전략 체계','글로벌 시장별 전략적 가격 결정 및 관리 시스템 구축','전략적 프라이싱 시스템',49,1),
(81,'QUICK','인도 시장 판매 목표 미달','딜러 동기부여 부족','인도 시장 딜러 프로모션','인도 현지 딜러 대상 판매 인센티브 및 프로모션 기획','인도 딜러 인센티브 프로그램',50,1),
(82,'BUILD','인도 시장 진출 가속 필요','인도 브랜드 인지도 낮음','인도 시장 브랜드 빌딩','인도 시장 브랜드 인지도·호감도 향상 장기 전략 수립','인도 브랜드 구축 3개년 계획',50,1),
(83,'QUICK','보조금 정책 변경 리스크','국가별 정보 업데이트 지연','유럽 EV 보조금 마케팅','유럽 각국 EV 보조금 정보 연계 마케팅 캠페인','유럽 전기차 보조금 연계 홍보',51,1),
(84,'BUILD','동남아 시장 기회 확대','현지 마케팅 역량 부족','동남아 시장 확대 전략','인도네시아·태국·베트남 마케팅 인프라 및 역량 구축','동남아 마케팅 인프라 구축',51,1),
(85,'QUICK','동남아 브랜드 인지도 낮음','현지 소셜 플랫폼 이해 부족','동남아 SNS 캠페인','동남아 주요국 현지 SNS 플랫폼 맞춤 캠페인 실행','인도네시아·태국 소셜 마케팅',52,1),
(86,'BUILD','유럽 EV 경쟁 치열','유럽 브랜드 대비 인지도 부족','유럽 EV 리더십 전략','유럽 전기차 시장 점유율 확대를 위한 장기 마케팅 전략','유럽 전기차 시장 공략',52,1),
(87,'QUICK','미국 시장 점유율 위협','경쟁사 공격적 마케팅','북미 시장 방어 마케팅','미국 시장 경쟁 심화 대응 마케팅 전략 수립','미국 시장 점유율 방어 전략',53,1),
(88,'BUILD','중동 럭셔리 시장 경쟁','프리미엄 포지셔닝 미흡','중동 럭셔리 전략','중동 시장 럭셔리 세그먼트 공략을 위한 마케팅 전략','중동 프리미엄 시장 공략',53,1),
(89,'QUICK','중동 럭셔리 시장 경쟁','VIP 고객 관리 체계 미흡','중동 럭셔리 이벤트','중동 지역 고소득 고객 대상 프리미엄 체험 이벤트','중동 VIP 고객 초청 행사',54,1),
(90,'BUILD','아프리카 시장 잠재력 확대','시장 진입 전략 부재','아프리카 시장 진출 로드맵','아프리카 주요국 시장 진출을 위한 단계별 마케팅 로드맵','아프리카 시장 개척 전략',54,1),
(91,'QUICK','호주 시장 성장 둔화','현지 미디어 비용 상승','호주 시장 캠페인 실행','호주 시장 맞춤 TV·디지털 광고 캠페인 실행','호주 현지 광고 캠페인',55,1),
(92,'BUILD','글로벌-로컬 갈등','협업 프레임워크 부재','글로벌-로컬 마케팅 체계','글로벌 브랜드 일관성과 현지 맞춤 마케팅의 균형 체계 구축','글로컬 마케팅 프레임워크',55,1),
(93,'QUICK','중남미 브랜드 인지도 낮음','현지 시장 데이터 부족','중남미 브랜드 인지도 조사','중남미 주요국 브랜드 인지도 및 이미지 조사','멕시코·브라질 브랜드 서베이',56,1),
(94,'BUILD','딜러 마케팅 역량 편차','딜러 지원 체계 미흡','현지 딜러 네트워크 강화','전 세계 딜러 마케팅 역량 강화 및 지원 체계 구축','글로벌 딜러 마케팅 역량 강화',56,1),
(95,'QUICK','현지 소비자 신뢰 구축 필요','인플루언서 네트워크 부재','현지 인플루언서 협업','각 지역 현지 자동차 인플루언서 협업 프로그램','지역별 인플루언서 마케팅',57,1),
(96,'BUILD','시장 진출 프로세스 비표준','진출 실패 리스크','신흥시장 진출 플레이북','신규 시장 진출 시 활용하는 마케팅 플레이북 개발','신시장 진입 표준 프로세스',57,1),
(97,'QUICK','마케팅 자산 중복 제작','자산 공유 플랫폼 미흡','글로벌 마케팅 자산 공유','본사 제작 마케팅 자산의 현지 법인 활용 체계 구축','글로벌 콘텐츠 허브 운영',58,1),
(98,'BUILD','디지털 국경 무력화','크로스보더 전략 부재','크로스보더 마케팅 전략','디지털 기반 국경을 초월한 글로벌 마케팅 전략 수립','국경 초월 디지털 마케팅',58,1),
(99,'QUICK','모터쇼 ROI 불명확','전시 전략 비표준화','현지 모터쇼 참가 전략','각 지역 주요 모터쇼 참가 전략 및 부스 설계','지역별 전시회 부스 기획',59,1),
(100,'BUILD','현지 전문 인력 부족','인재 육성 체계 미흡','글로벌 마케팅 인재 양성','각 지역 현지 마케팅 전문 인력 양성 프로그램 구축','현지 마케팅 전문 인력 육성',59,1),
(101,'QUICK','리포트 작성 지연','수작업 데이터 집계','월간 마케팅 성과 리포트','전사 마케팅 활동 월간 성과 리포트 자동화','마케팅 KPI 월간 보고서',60,1),
(102,'BUILD','데이터 분산 관리','통합 DW 부재','마케팅 데이터 웨어하우스','전사 마케팅 데이터를 통합 관리하는 데이터 웨어하우스 구축','통합 마케팅 DW 구축',60,1),
(103,'QUICK','테스트 문화 부재','테스트 결과 활용 미흡','캠페인 A/B 테스트 체계','디지털 캠페인 A/B 테스트 표준 프로세스 수립','광고 소재 테스트 프레임워크',61,1),
(104,'BUILD','사후 분석 중심','예측 역량 부재','예측 분석 모델 구축','AI 기반 마케팅 성과 예측 및 자동 최적화 모델 개발','마케팅 예측 AI 모델',61,1),
(105,'QUICK','미디어 비용 대비 효과 불명','채널별 성과 측정 어려움','미디어 효율 분석','TV·디지털·옥외 등 채널별 미디어 효율 비교 분석','미디어 채널별 ROI 분석',62,1),
(106,'BUILD','성과 관리 파편화','통합 플랫폼 부재','통합 성과 관리 시스템','전사 마케팅 성과를 통합 관리하는 디지털 플랫폼 구축','마케팅 성과 관리 플랫폼',62,1),
(107,'QUICK','고객 이해 부족','데이터 사일로 문제','고객 세그먼트 분석','구매 데이터 기반 고객 세그먼트 분석 및 프로파일링','타겟 고객군 프로파일링',63,1),
(108,'BUILD','어트리뷰션 측정 불가','라스트클릭 모델 한계','마케팅 어트리뷰션 체계','고객 구매 여정의 각 터치포인트 기여도 측정 체계 구축','멀티터치 어트리뷰션 모델',63,1),
(109,'QUICK','경쟁 정보 부족','체계적 추적 미흡','경쟁사 마케팅 인텔리전스','주요 경쟁사 마케팅 캠페인·예산·전략 정기 분석','경쟁사 마케팅 활동 추적',64,1),
(110,'BUILD','AI 활용 역량 부족','전문 조직 부재','마케팅 AI 센터 구축','마케팅 AI 활용을 전담하는 센터 오브 엑설런스 구축','AI CoE 마케팅 전문 조직',64,1),
(111,'QUICK','브랜드 지표 하락 감지 지연','조사 주기 불규칙','브랜드 트래킹 조사','브랜드 인지도·호감도·구매의향 정기 트래킹 조사','분기별 브랜드 건강도 측정',65,1),
(112,'BUILD','의사결정 지연','실시간 분석 불가','리얼타임 마케팅 시스템','실시간 데이터 기반 마케팅 의사결정 지원 시스템 구축','실시간 마케팅 의사결정 플랫폼',65,1),
(113,'QUICK','예산 초과 집행 리스크','집행 현황 파악 지연','마케팅 예산 집행 현황','월별 마케팅 예산 집행 현황 실시간 모니터링 체계','예산 대비 집행률 모니터링',66,1),
(114,'BUILD','미디어 예산 배분 비효율','MMM 모델 부재','마케팅 믹스 모델링','미디어 채널 간 최적 예산 배분을 위한 MMM 모델 구축','미디어 최적 배분 모델',66,1),
(115,'QUICK','소셜 성과 체계적 분석 부재','분석 툴 미도입','소셜미디어 분석 리포트','채널별 팔로워·도달·인게이지먼트 등 성과 분석','SNS 채널 성과 분석',67,1),
(116,'BUILD','고객 가치 평가 불가','LTV 모델 부재','고객 LTV 예측 시스템','고객별 생애가치를 예측하고 세그먼트별 전략을 수립','고객 생애가치 예측 모델',67,1),
(117,'QUICK','학습 내용 소실','사후 분석 생략 경향','캠페인 학습 체계 구축','완료 캠페인의 성과·인사이트 축적 및 공유 체계','캠페인 사후 분석 프로세스',68,1),
(118,'BUILD','글로벌 성과 비교 불가','BI 표준화 미흡','글로벌 마케팅 BI 체계','전 세계 마케팅 성과를 일관된 기준으로 분석하는 BI 체계','글로벌 BI 리포팅 플랫폼',68,1),
(119,'QUICK','지역별 성과 비교 어려움','데이터 기준 불일치','글로벌 성과 비교 대시보드','글로벌 각 지역 마케팅 성과 실시간 비교 대시보드','지역별 마케팅 성과 비교',69,1),
(120,'BUILD','실험 문화 부재','테스트 자동화 미흡','마케팅 실험 플랫폼','대규모 마케팅 실험을 자동화하고 학습하는 플랫폼 구축','A/B 테스트 자동화 시스템',69,1);

-- task_activity (600 rows) - 모든 캔버스 동일한 5개 활동
INSERT INTO task_activity (activity_content, canvas_id, duration, order_no, process_step)
SELECT a.content, wc.canvas_id, a.dur, a.ord, a.step
FROM win_canvas wc
CROSS JOIN (
  SELECT '프로젝트 범위 및 목표 설정' AS content, '2주' AS dur, 1 AS ord, '기획' AS step UNION ALL
  SELECT '시장 조사 및 데이터 분석','3주',2,'조사·분석' UNION ALL
  SELECT '실행 전략 및 계획 수립','2주',3,'전략 수립' UNION ALL
  SELECT '캠페인·프로그램 실행','4주',4,'실행' UNION ALL
  SELECT '성과 측정 및 결과 보고','1주',5,'평가·보고'
) a WHERE wc.canvas_id BETWEEN 1 AND 120;

-- task_input (600 rows) - 팀별 5개 리소스
INSERT INTO task_input (canvas_id, order_no, quantity, resource_name)
SELECT wc.canvas_id, i.ord, i.qty, i.res FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,3 AS qty,'마케팅 전문 인력' AS res UNION ALL SELECT 2,5000,'브랜드 캠페인 예산' UNION ALL SELECT 3,1,'브랜드 관리 시스템' UNION ALL SELECT 4,2,'시장 조사 데이터' UNION ALL SELECT 5,1,'외부 광고 에이전시'
) i WHERE wc.canvas_id BETWEEN 1 AND 20;
INSERT INTO task_input (canvas_id, order_no, quantity, resource_name)
SELECT wc.canvas_id, i.ord, i.qty, i.res FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,4 AS qty,'디지털 마케터' AS res UNION ALL SELECT 2,3000,'디지털 광고 예산' UNION ALL SELECT 3,2,'마테크 플랫폼' UNION ALL SELECT 4,3,'웹 분석 데이터' UNION ALL SELECT 5,1,'디지털 에이전시'
) i WHERE wc.canvas_id BETWEEN 21 AND 40;
INSERT INTO task_input (canvas_id, order_no, quantity, resource_name)
SELECT wc.canvas_id, i.ord, i.qty, i.res FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,3 AS qty,'CX 전문 인력' AS res UNION ALL SELECT 2,2000,'고객 조사 예산' UNION ALL SELECT 3,1,'CRM 시스템' UNION ALL SELECT 4,5,'고객 피드백 데이터' UNION ALL SELECT 5,1,'서비스 컨설팅'
) i WHERE wc.canvas_id BETWEEN 41 AND 60;
INSERT INTO task_input (canvas_id, order_no, quantity, resource_name)
SELECT wc.canvas_id, i.ord, i.qty, i.res FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,4 AS qty,'제품 마케터' AS res UNION ALL SELECT 2,4000,'론칭 캠페인 예산' UNION ALL SELECT 3,1,'제품 정보 시스템' UNION ALL SELECT 4,3,'경쟁 분석 데이터' UNION ALL SELECT 5,2,'미디어 에이전시'
) i WHERE wc.canvas_id BETWEEN 61 AND 80;
INSERT INTO task_input (canvas_id, order_no, quantity, resource_name)
SELECT wc.canvas_id, i.ord, i.qty, i.res FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,5 AS qty,'현지 마케팅 인력' AS res UNION ALL SELECT 2,3500,'현지 마케팅 예산' UNION ALL SELECT 3,1,'글로벌 자산 관리 시스템' UNION ALL SELECT 4,4,'현지 시장 데이터' UNION ALL SELECT 5,2,'현지 에이전시'
) i WHERE wc.canvas_id BETWEEN 81 AND 100;
INSERT INTO task_input (canvas_id, order_no, quantity, resource_name)
SELECT wc.canvas_id, i.ord, i.qty, i.res FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,4 AS qty,'데이터 분석가' AS res UNION ALL SELECT 2,2500,'분석 도구 예산' UNION ALL SELECT 3,2,'BI 플랫폼' UNION ALL SELECT 4,5,'마케팅 성과 데이터' UNION ALL SELECT 5,1,'분석 컨설팅'
) i WHERE wc.canvas_id BETWEEN 101 AND 120;

-- task_outcome (720 rows) - 팀별 6개 성과
INSERT INTO task_outcome (canvas_id, order_no, outcome_content, outcome_type)
SELECT wc.canvas_id, o.ord, o.content, o.otype FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,'브랜드 인지도 10% 향상' AS content,'QUANTITATIVE' AS otype UNION ALL SELECT 2,'캠페인 도달률 70% 달성','QUANTITATIVE' UNION ALL SELECT 3,'미디어 가치 15% 증가','QUANTITATIVE' UNION ALL
  SELECT 4,'프리미엄 브랜드 이미지 강화','QUALITATIVE' UNION ALL SELECT 5,'글로벌 브랜드 일관성 확보','QUALITATIVE' UNION ALL SELECT 6,'브랜드 스토리텔링 역량 향상','QUALITATIVE'
) o WHERE wc.canvas_id BETWEEN 1 AND 20;
INSERT INTO task_outcome (canvas_id, order_no, outcome_content, outcome_type)
SELECT wc.canvas_id, o.ord, o.content, o.otype FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,'디지털 전환율 3% 달성' AS content,'QUANTITATIVE' AS otype UNION ALL SELECT 2,'디지털 리드 50% 증가','QUANTITATIVE' UNION ALL SELECT 3,'디지털 광고 ROI 20% 개선','QUANTITATIVE' UNION ALL
  SELECT 4,'디지털 마케팅 역량 고도화','QUALITATIVE' UNION ALL SELECT 5,'데이터 기반 의사결정 문화 정착','QUALITATIVE' UNION ALL SELECT 6,'고객 디지털 경험 향상','QUALITATIVE'
) o WHERE wc.canvas_id BETWEEN 21 AND 40;
INSERT INTO task_outcome (canvas_id, order_no, outcome_content, outcome_type)
SELECT wc.canvas_id, o.ord, o.content, o.otype FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,'고객 만족도 90점 달성' AS content,'QUANTITATIVE' AS otype UNION ALL SELECT 2,'NPS 50점 이상 달성','QUANTITATIVE' UNION ALL SELECT 3,'고객 이탈률 10% 감소','QUANTITATIVE' UNION ALL
  SELECT 4,'프리미엄 고객 경험 실현','QUALITATIVE' UNION ALL SELECT 5,'고객 중심 조직 문화 강화','QUALITATIVE' UNION ALL SELECT 6,'서비스 혁신 역량 확보','QUALITATIVE'
) o WHERE wc.canvas_id BETWEEN 41 AND 60;
INSERT INTO task_outcome (canvas_id, order_no, outcome_content, outcome_type)
SELECT wc.canvas_id, o.ord, o.content, o.otype FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,'신차 사전계약 5만건 달성' AS content,'QUANTITATIVE' AS otype UNION ALL SELECT 2,'EV 판매 비중 30% 달성','QUANTITATIVE' UNION ALL SELECT 3,'제품 인지도 70% 달성','QUANTITATIVE' UNION ALL
  SELECT 4,'기술 리더십 이미지 강화','QUALITATIVE' UNION ALL SELECT 5,'제품 차별화 인식 확보','QUALITATIVE' UNION ALL SELECT 6,'론칭 프로세스 혁신','QUALITATIVE'
) o WHERE wc.canvas_id BETWEEN 61 AND 80;
INSERT INTO task_outcome (canvas_id, order_no, outcome_content, outcome_type)
SELECT wc.canvas_id, o.ord, o.content, o.otype FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,'지역 판매량 20% 성장' AS content,'QUANTITATIVE' AS otype UNION ALL SELECT 2,'현지 인지도 15% 향상','QUANTITATIVE' UNION ALL SELECT 3,'현지 마케팅 ROI 15% 개선','QUANTITATIVE' UNION ALL
  SELECT 4,'글로벌-로컬 마케팅 체계 구축','QUALITATIVE' UNION ALL SELECT 5,'현지 시장 이해도 향상','QUALITATIVE' UNION ALL SELECT 6,'현지 파트너 관계 강화','QUALITATIVE'
) o WHERE wc.canvas_id BETWEEN 81 AND 100;
INSERT INTO task_outcome (canvas_id, order_no, outcome_content, outcome_type)
SELECT wc.canvas_id, o.ord, o.content, o.otype FROM win_canvas wc CROSS JOIN (
  SELECT 1 AS ord,'마케팅 ROI 10% 개선' AS content,'QUANTITATIVE' AS otype UNION ALL SELECT 2,'리포트 자동화율 80% 달성','QUANTITATIVE' UNION ALL SELECT 3,'데이터 정확도 95% 달성','QUANTITATIVE' UNION ALL
  SELECT 4,'데이터 기반 의사결정 체계 구축','QUALITATIVE' UNION ALL SELECT 5,'분석 역량 내재화','QUALITATIVE' UNION ALL SELECT 6,'성과 관리 문화 정착','QUALITATIVE'
) o WHERE wc.canvas_id BETWEEN 101 AND 120;

-- teamwork (120 rows) - 팀별 10개 패턴 순환
INSERT INTO teamwork (activity_teamwork, canvas_id, work_type)
SELECT t.act, wc.canvas_id, t.wtype FROM win_canvas wc
JOIN (SELECT 1 AS pos,'브랜드 전략 공동 수립' AS act,'공동기획' AS wtype UNION ALL SELECT 2,'글로벌 캠페인 합동 기획','협업실행' UNION ALL SELECT 3,'브랜드 가이드라인 공동 검토','합동분석' UNION ALL SELECT 4,'브랜드 성과 합동 분석','공동보고' UNION ALL SELECT 5,'크로스팀 브랜드 워크숍','크로스팀 협업' UNION ALL SELECT 6,'브랜드 위기 대응 합동 훈련','데이터 공유' UNION ALL SELECT 7,'미디어 전략 공동 리뷰','공동 워크숍' UNION ALL SELECT 8,'브랜드 경험 공동 설계','합동 리뷰' UNION ALL SELECT 9,'ESG 브랜드 협업 기획','협업 브레인스토밍' UNION ALL SELECT 10,'스폰서십 효과 합동 분석','팀간 벤치마킹'
) t ON ((wc.canvas_id - 1) % 10) + 1 = t.pos WHERE wc.canvas_id BETWEEN 1 AND 20;
INSERT INTO teamwork (activity_teamwork, canvas_id, work_type)
SELECT t.act, wc.canvas_id, t.wtype FROM win_canvas wc
JOIN (SELECT 1 AS pos,'디지털 전략 공동 수립' AS act,'공동기획' AS wtype UNION ALL SELECT 2,'콘텐츠 제작 협업','협업실행' UNION ALL SELECT 3,'디지털 성과 합동 분석','합동분석' UNION ALL SELECT 4,'마테크 도입 공동 검토','공동보고' UNION ALL SELECT 5,'크로스채널 전략 워크숍','크로스팀 협업' UNION ALL SELECT 6,'데이터 활용 공동 기획','데이터 공유' UNION ALL SELECT 7,'CDP 구축 협업','공동 워크숍' UNION ALL SELECT 8,'디지털 광고 합동 최적화','합동 리뷰' UNION ALL SELECT 9,'자동화 시스템 공동 설계','협업 브레인스토밍' UNION ALL SELECT 10,'디지털 혁신 브레인스토밍','팀간 벤치마킹'
) t ON ((wc.canvas_id - 1) % 10) + 1 = t.pos WHERE wc.canvas_id BETWEEN 21 AND 40;
INSERT INTO teamwork (activity_teamwork, canvas_id, work_type)
SELECT t.act, wc.canvas_id, t.wtype FROM win_canvas wc
JOIN (SELECT 1 AS pos,'고객 여정 공동 분석' AS act,'공동기획' AS wtype UNION ALL SELECT 2,'VOC 분석 협업','협업실행' UNION ALL SELECT 3,'CX 개선 합동 워크숍','합동분석' UNION ALL SELECT 4,'딜러 교육 공동 기획','공동보고' UNION ALL SELECT 5,'서비스 혁신 크로스팀 협업','크로스팀 협업' UNION ALL SELECT 6,'고객 데이터 공유 협업','데이터 공유' UNION ALL SELECT 7,'로열티 프로그램 합동 설계','공동 워크숍' UNION ALL SELECT 8,'고객 응대 표준 공동 수립','합동 리뷰' UNION ALL SELECT 9,'서비스 디자인 공동 워크숍','협업 브레인스토밍' UNION ALL SELECT 10,'CX 지표 합동 리뷰','팀간 벤치마킹'
) t ON ((wc.canvas_id - 1) % 10) + 1 = t.pos WHERE wc.canvas_id BETWEEN 41 AND 60;
INSERT INTO teamwork (activity_teamwork, canvas_id, work_type)
SELECT t.act, wc.canvas_id, t.wtype FROM win_canvas wc
JOIN (SELECT 1 AS pos,'신차 론칭 합동 기획' AS act,'공동기획' AS wtype UNION ALL SELECT 2,'제품 포지셔닝 공동 분석','협업실행' UNION ALL SELECT 3,'시승 프로그램 협업 운영','합동분석' UNION ALL SELECT 4,'경쟁 분석 합동 리뷰','공동보고' UNION ALL SELECT 5,'기술 마케팅 크로스팀 협업','크로스팀 협업' UNION ALL SELECT 6,'EV 마케팅 공동 전략','데이터 공유' UNION ALL SELECT 7,'제품 라인업 합동 검토','공동 워크숍' UNION ALL SELECT 8,'가격 전략 공동 분석','합동 리뷰' UNION ALL SELECT 9,'미디어 바잉 협업','협업 브레인스토밍' UNION ALL SELECT 10,'론칭 캠페인 합동 리허설','팀간 벤치마킹'
) t ON ((wc.canvas_id - 1) % 10) + 1 = t.pos WHERE wc.canvas_id BETWEEN 61 AND 80;
INSERT INTO teamwork (activity_teamwork, canvas_id, work_type)
SELECT t.act, wc.canvas_id, t.wtype FROM win_canvas wc
JOIN (SELECT 1 AS pos,'글로벌-로컬 전략 공동 수립' AS act,'공동기획' AS wtype UNION ALL SELECT 2,'현지화 콘텐츠 협업 제작','협업실행' UNION ALL SELECT 3,'지역 성과 합동 분석','합동분석' UNION ALL SELECT 4,'현지 딜러 공동 교육','공동보고' UNION ALL SELECT 5,'크로스 지역 벤치마킹','크로스팀 협업' UNION ALL SELECT 6,'마케팅 자산 공유 협업','데이터 공유' UNION ALL SELECT 7,'현지 미디어 합동 기획','공동 워크숍' UNION ALL SELECT 8,'글로벌 캠페인 현지화 협업','합동 리뷰' UNION ALL SELECT 9,'지역별 인사이트 공유','협업 브레인스토밍' UNION ALL SELECT 10,'현지 파트너 합동 관리','팀간 벤치마킹'
) t ON ((wc.canvas_id - 1) % 10) + 1 = t.pos WHERE wc.canvas_id BETWEEN 81 AND 100;
INSERT INTO teamwork (activity_teamwork, canvas_id, work_type)
SELECT t.act, wc.canvas_id, t.wtype FROM win_canvas wc
JOIN (SELECT 1 AS pos,'성과 분석 공동 리뷰' AS act,'공동기획' AS wtype UNION ALL SELECT 2,'데이터 수집 협업','협업실행' UNION ALL SELECT 3,'대시보드 공동 설계','합동분석' UNION ALL SELECT 4,'분석 방법론 합동 워크숍','공동보고' UNION ALL SELECT 5,'크로스팀 데이터 공유','크로스팀 협업' UNION ALL SELECT 6,'KPI 체계 공동 수립','데이터 공유' UNION ALL SELECT 7,'예산 분석 협업','공동 워크숍' UNION ALL SELECT 8,'실험 설계 합동 리뷰','합동 리뷰' UNION ALL SELECT 9,'인사이트 공유 워크숍','협업 브레인스토밍' UNION ALL SELECT 10,'글로벌 성과 합동 비교','팀간 벤치마킹'
) t ON ((wc.canvas_id - 1) % 10) + 1 = t.pos WHERE wc.canvas_id BETWEEN 101 AND 120;

-- ============================================
-- f_letter_of_intent (240 rows)
-- 60명 × 4건 (BUILD 2 + QUICK 2)
-- 팀당 20 BUILD + 20 QUICK 수신 (균등 배분)
-- ============================================

-- BUILD 투자 (120 rows)
INSERT INTO f_letter_of_intent (category_cd, course_cd, del_yn, game_id, investment_price, investment_target, opinion, reg_dt, score1, score2, score3, score4, score5, score6, score7, score8, score9, user_id, team_id, canvas_id, submitted, canvas_type)
WITH user_team AS (
  SELECT tu.user_id, tu.team_id,
    tu.user_id - MIN(tu.user_id) OVER (PARTITION BY tu.team_id) AS pos
  FROM teamuser tu
  JOIN gameteam gt ON tu.team_id = gt.team_id AND gt.game_id = 2
),
other_teams AS (
  SELECT ut.user_id, ut.team_id AS my_team, ut.pos,
    ot.team_id AS target_team,
    ROW_NUMBER() OVER (PARTITION BY ut.user_id ORDER BY ot.team_id) - 1 AS rn
  FROM user_team ut
  JOIN gameteam ot ON ot.game_id = 2 AND ot.team_id != ut.team_id
)
SELECT 'BUILD', '2JZFUB', 'N', 2,
  ELT(1 + ((o.user_id * 7 + o.target_team) % 6), '2000','3000','4000','5000','6000','7000'),
  o.target_team,
  ELT(1 + ((o.user_id + o.target_team) % 10),
    '해당 팀의 장기 전략이 우수하여 투자 가치가 높다고 판단',
    '지속가능한 성장 전략과 실행력이 인상적',
    '혁신적인 접근 방식과 팀 역량이 뛰어남',
    '시장 기회를 잘 포착한 전략적 방향성이 우수',
    '고객 가치 창출 관점에서 높은 성과 기대',
    '단기 성과 창출 가능성이 높은 프로젝트',
    '빠른 실행력과 명확한 성과 지표가 우수',
    '시장 변화에 신속하게 대응하는 전략이 인상적',
    '효율적인 자원 활용과 실행 계획이 탁월',
    '즉각적인 고객 가치 창출이 기대됨'),
  '2026-02-20 10:00:00.000000',
  3 + ((o.user_id*3 + o.target_team*2 + 1) % 3),
  3 + ((o.user_id*5 + o.target_team*3 + 2) % 3),
  3 + ((o.user_id*7 + o.target_team + 3) % 3),
  3 + ((o.user_id*2 + o.target_team*5 + 4) % 3),
  3 + ((o.user_id*4 + o.target_team*7 + 5) % 3),
  3 + ((o.user_id*6 + o.target_team*4 + 6) % 3),
  3 + ((o.user_id*8 + o.target_team*6 + 7) % 3),
  3 + ((o.user_id + o.target_team*8 + 8) % 3),
  3 + ((o.user_id*9 + o.target_team*9 + 9) % 3),
  o.user_id,
  o.my_team,
  ((o.target_team - 4) * 10) * 2 + 2,
  1,
  'BUILD'
FROM other_teams o
WHERE o.rn = o.pos % 5 OR o.rn = (o.pos + 1) % 5;

-- QUICK 투자 (120 rows)
INSERT INTO f_letter_of_intent (category_cd, course_cd, del_yn, game_id, investment_price, investment_target, opinion, reg_dt, score1, score2, score3, score4, score5, score6, score7, score8, score9, user_id, team_id, canvas_id, submitted, canvas_type)
WITH user_team AS (
  SELECT tu.user_id, tu.team_id,
    tu.user_id - MIN(tu.user_id) OVER (PARTITION BY tu.team_id) AS pos
  FROM teamuser tu
  JOIN gameteam gt ON tu.team_id = gt.team_id AND gt.game_id = 2
),
other_teams AS (
  SELECT ut.user_id, ut.team_id AS my_team, ut.pos,
    ot.team_id AS target_team,
    ROW_NUMBER() OVER (PARTITION BY ut.user_id ORDER BY ot.team_id) - 1 AS rn
  FROM user_team ut
  JOIN gameteam ot ON ot.game_id = 2 AND ot.team_id != ut.team_id
)
SELECT 'QUICK', '2JZFUB', 'N', 2,
  ELT(1 + ((o.user_id * 5 + o.target_team * 3) % 6), '2000','3000','4000','5000','6000','7000'),
  o.target_team,
  ELT(1 + ((o.user_id * 3 + o.target_team) % 10),
    '단기 성과 창출 가능성이 높은 프로젝트',
    '빠른 실행력과 명확한 성과 지표가 우수',
    '시장 변화에 신속하게 대응하는 전략이 인상적',
    '효율적인 자원 활용과 실행 계획이 탁월',
    '즉각적인 고객 가치 창출이 기대됨',
    '해당 팀의 장기 전략이 우수하여 투자 가치가 높다고 판단',
    '지속가능한 성장 전략과 실행력이 인상적',
    '혁신적인 접근 방식과 팀 역량이 뛰어남',
    '시장 기회를 잘 포착한 전략적 방향성이 우수',
    '고객 가치 창출 관점에서 높은 성과 기대'),
  '2026-02-20 10:00:00.000000',
  3 + ((o.user_id*4 + o.target_team*3 + 2) % 3),
  3 + ((o.user_id*6 + o.target_team*5 + 1) % 3),
  3 + ((o.user_id*2 + o.target_team*7 + 4) % 3),
  3 + ((o.user_id*8 + o.target_team*2 + 3) % 3),
  3 + ((o.user_id*3 + o.target_team*4 + 6) % 3),
  3 + ((o.user_id*7 + o.target_team*6 + 5) % 3),
  3 + ((o.user_id*5 + o.target_team*8 + 8) % 3),
  3 + ((o.user_id*9 + o.target_team + 7) % 3),
  3 + ((o.user_id + o.target_team*9 + 9) % 3),
  o.user_id,
  o.my_team,
  ((o.target_team - 4) * 10) * 2 + 1,
  1,
  'QUICK'
FROM other_teams o
WHERE o.rn = (o.pos + 2) % 5 OR o.rn = (o.pos + 3) % 5;

SELECT '모든 seed 데이터 삽입 완료! (f_letter_of_intent 240행 포함)' AS result;
