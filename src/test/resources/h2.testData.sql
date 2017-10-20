-- This script is run on the H2 DB to setup test data
-- NOTE
-- You can use this data to simplify test setup. Do NOT modify this data during test
-- Test MUST leave the test DB in the same state as it was before test start
--
-- @see: jpa-test.h2.properties

INSERT INTO DBO.DEALSTATUS VALUES (1, 'Cancelled', 6);
INSERT INTO DBO.DEALSTATUS VALUES (2, 'Closed', 5);
INSERT INTO DBO.DEALSTATUS VALUES (3, 'In Syndication', 3);
INSERT INTO DBO.DEALSTATUS VALUES (4, 'Mandated', 2);
INSERT INTO DBO.DEALSTATUS VALUES (5, 'Market-Sounding', 1);
INSERT INTO DBO.DEALSTATUS VALUES (6, 'On Hold', 7);
INSERT INTO DBO.DEALSTATUS VALUES (7, 'Synd Closed - Docs Pending', 4);
INSERT INTO DBO.DEALSTATUS VALUES (8, 'Market Intelligence (Non-Barcap)', 0);

INSERT INTO DBO.Seniority VALUES (1, 'Senior Secured', 1);
INSERT INTO DBO.Seniority VALUES (2, 'SeniorSubordinated', 2);
INSERT INTO DBO.Seniority VALUES (3, 'SeniorUnsecured', 3);
INSERT INTO DBO.Seniority VALUES (4, 'SeniorJunior', 4);

INSERT INTO DBO.COUNTERPARTY (Id, Name, Type, AOTApproved, Active, IsBank, IsMonet, IsAtlas, IsProcessAgentRequired, IsClearParEligibleUS, IsClearParEligibleUK) VALUES (1001, 'Test CounterParty', 'L', 0, 1, 1, 0, 0, 1, 1, 0);
INSERT INTO DBO.COUNTERPARTY (Id, Name, Type, AOTApproved, Active, IsBank, IsMonet, IsAtlas, IsProcessAgentRequired, IsClearParEligibleUS, IsClearParEligibleUK) VALUES (1002, 'Test CounterParty', 'L', 0, 1, 1, 0, 0, 1, 1, 0);

INSERT INTO IBSD.Deal (Id, Active, AgentSDSId, Amount, CUSIP, DealStatusId, Description, IsClearParEligibleUK, IsClearparEligibleUS, IsEnrichmentComplete, IsOriginalLender, SeniorityId) VALUES (1001, 1, 123456, 1000, 'A12345', 3, 'Test Deal', 0, 1, 0, 1, 1);