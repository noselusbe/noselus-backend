UPDATE written_question SET question_text = REGEXP_REPLACE ( question_text , '^(<br />)*' , '' );
UPDATE written_question SET question_text = REGEXP_REPLACE ( question_text , '(<br />)*$' , '' );