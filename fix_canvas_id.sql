-- Game 5 f_letter_of_intent / f_letter_of_intent2 canvas_id 수정
-- 기존: 평가자 본인 캔버스 → 수정: 대상 팀 대표작성자(writer) 캔버스

-- =============================================
-- f_letter_of_intent (BUILD) canvas_id 수정
-- =============================================
UPDATE f_letter_of_intent SET canvas_id = 22  WHERE game_id = 5 AND investment_target = '15';
UPDATE f_letter_of_intent SET canvas_id = 42  WHERE game_id = 5 AND investment_target = '16';
UPDATE f_letter_of_intent SET canvas_id = 62  WHERE game_id = 5 AND investment_target = '17';
UPDATE f_letter_of_intent SET canvas_id = 82  WHERE game_id = 5 AND investment_target = '18';
UPDATE f_letter_of_intent SET canvas_id = 102 WHERE game_id = 5 AND investment_target = '19';
UPDATE f_letter_of_intent SET canvas_id = 122 WHERE game_id = 5 AND investment_target = '20';

-- =============================================
-- f_letter_of_intent2 (QUICK) canvas_id 수정
-- =============================================
UPDATE f_letter_of_intent2 SET canvas_id = 21  WHERE game_id = 5 AND investment_target = '15';
UPDATE f_letter_of_intent2 SET canvas_id = 41  WHERE game_id = 5 AND investment_target = '16';
UPDATE f_letter_of_intent2 SET canvas_id = 61  WHERE game_id = 5 AND investment_target = '17';
UPDATE f_letter_of_intent2 SET canvas_id = 81  WHERE game_id = 5 AND investment_target = '18';
UPDATE f_letter_of_intent2 SET canvas_id = 101 WHERE game_id = 5 AND investment_target = '19';
UPDATE f_letter_of_intent2 SET canvas_id = 121 WHERE game_id = 5 AND investment_target = '20';
