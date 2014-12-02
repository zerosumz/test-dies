SELECT POST TEST
================

* this is a simple test to select posts
* post has some comments
* so we test expected comments exists when mapper method calls. 

| ID[^int] |   CONTENT[^String]   |
|----------|----------------------|
|        1 | hey, i must do test. |
[POST]


| ID[^int] | POST_ID[^int] |      CONTENT[^String]     |
|----------|---------------|---------------------------|
|        1 |             1 | oh, i'going to ready      |
|        2 |             1 | give me just two sec.     |
|        3 |             1 | not my business. get off. |
[COMMENT]


* TESTS
    * there is 3 comments?

[^int]:java.lang.Integer

[^String]:java.lang.String
