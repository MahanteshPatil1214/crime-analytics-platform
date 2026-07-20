-- Karnataka Police FIR System - Seed Data

-- ============================================================
-- STATE
-- ============================================================
INSERT INTO state (state_id, state_name, nationality_id, active) VALUES
(1, 'Karnataka', 1, '1'),
(2, 'Maharashtra', 1, '1'),
(3, 'Tamil Nadu', 1, '1'),
(4, 'Kerala', 1, '1'),
(5, 'Andhra Pradesh', 1, '1');

-- ============================================================
-- DISTRICT
-- ============================================================
INSERT INTO district (district_id, district_name, state_id, active) VALUES
(1, 'Bengaluru Urban', 1, '1'),
(2, 'Kalaburgi', 1, '1'),
(3, 'Mysuru', 1, '1'),
(4, 'Mangaluru', 1, '1'),
(5, 'Hubballi-Dharwad', 1, '1'),
(6, 'Belagavi', 1, '1'),
(7, 'Shivamogga', 1, '1'),
(8, 'Ballari', 1, '1'),
(9, 'Vijayapura', 1, '1'),
(10, 'Raichur', 1, '1');

-- ============================================================
-- UNIT TYPE
-- ============================================================
INSERT INTO unit_type (unit_type_id, unit_type_name, city_dist_state, hierarchy, active) VALUES
(1, 'Police Station', 'City', 5, '1'),
(2, 'Circle Office', 'City', 4, '1'),
(3, 'Sub-Division', 'District', 3, '1'),
(4, 'District Command', 'District', 2, '1'),
(5, 'State HQ', 'State', 1, '1');

-- ============================================================
-- UNIT (Police Stations & Offices)
-- ============================================================
INSERT INTO unit (unit_id, unit_name, type_id, parent_unit, state_id, district_id, active) VALUES
-- Bengaluru Urban
(1, 'Lalbagh Police Station', 1, 2, 1, 1, '1'),
(2, 'Bengaluru Urban Division', 2, 3, 1, 1, '1'),
(3, 'Bengaluru City Range', 3, NULL, 1, 1, '1'),
-- Kalaburgi
(4, 'Kalaburgi Town PS', 1, 5, 1, 2, '1'),
(5, 'Kalaburgi Division', 2, 6, 1, 2, '1'),
(6, 'Kalaburgi Range', 3, NULL, 1, 2, '1'),
-- Mysuru
(7, 'Mysuru North PS', 1, 8, 1, 3, '1'),
(8, 'Mysuru City Division', 2, 9, 1, 3, '1'),
(9, 'Mysuru Range', 3, NULL, 1, 3, '1'),
-- Mangaluru
(10, 'Mangaluru Central PS', 1, 11, 1, 4, '1'),
(11, 'Mangaluru Division', 2, NULL, 1, 4, '1'),
-- Hubballi-Dharwad
(12, 'Hubballi Traffic PS', 1, 13, 1, 5, '1'),
(13, 'Dharwad Division', 2, NULL, 1, 5, '1'),
-- Belagavi
(14, 'Belagavi City PS', 1, NULL, 1, 6, '1'),
-- Shivamogga
(15, 'Shivamogga Town PS', 1, NULL, 1, 7, '1'),
-- Ballari
(16, 'Ballari Town PS', 1, NULL, 1, 8, '1'),
-- Vijayapura
(17, 'Vijayapura City PS', 1, NULL, 1, 9, '1'),
-- Raichur
(18, 'Raichur Town PS', 1, NULL, 1, 10, '1');

-- ============================================================
-- RANK
-- ============================================================
INSERT INTO rank (rank_id, rank_name, hierarchy, active) VALUES
(1, 'Director General of Police', 1, '1'),
(2, 'Additional Director General of Police', 2, '1'),
(3, 'Inspector General of Police', 3, '1'),
(4, 'Deputy Inspector General of Police', 4, '1'),
(5, 'Superintendent of Police', 5, '1'),
(6, 'Deputy Superintendent of Police', 6, '1'),
(7, 'Assistant Superintendent of Police', 7, '1'),
(8, 'Inspector', 8, '1'),
(9, 'Sub-Inspector', 9, '1'),
(10, 'Assistant Sub-Inspector', 10, '1'),
(11, 'Head Constable', 11, '1'),
(12, 'Constable', 12, '1');

-- ============================================================
-- DESIGNATION
-- ============================================================
INSERT INTO designation (designation_id, designation_name, active, sort_order) VALUES
(1, 'Commissioner of Police', '1', 1),
(2, 'Deputy Commissioner of Police', '1', 2),
(3, 'Assistant Commissioner of Police', '1', 3),
(4, 'Station House Officer', '1', 4),
(5, 'Investigating Officer', '1', 5),
(6, 'Patrol Officer', '1', 6),
(7, 'Crime Officer', '1', 7),
(8, 'Traffic Officer', '1', 8),
(9, 'Vice Officer', '1', 9);

-- ============================================================
-- CASE CATEGORY
-- ============================================================
INSERT INTO case_category (case_category_id, lookup_value) VALUES
(1, 'FIR'),
(2, 'UDR'),
(3, 'PAR'),
(4, 'Zero FIR'),
(5, 'NCR');

-- ============================================================
-- GRAVITY OFFENCE
-- ============================================================
INSERT INTO gravity_offence (gravity_offence_id, lookup_value) VALUES
(1, 'Heinous'),
(2, 'Non-Heinous'),
(3, 'Compoundable'),
(4, 'Cognizable Non-Compoundable');

-- ============================================================
-- CASE STATUS
-- ============================================================
INSERT INTO case_status_master (case_status_id, case_status_name) VALUES
(1, 'Registered'),
(2, 'Under Investigation'),
(3, 'Charge Sheeted'),
(4, 'Closed - Convicted'),
(5, 'Closed - Acquitted'),
(6, 'Closed - False Case'),
(7, 'Undetected'),
(8, 'Transfer Pending');

-- ============================================================
-- CASTE MASTER
-- ============================================================
INSERT INTO caste_master (caste_master_id, caste_master_name) VALUES
(1, 'General'),
(2, 'SC'),
(3, 'ST'),
(4, 'OBC'),
(5, 'EWS'),
(6, 'Lingayat'),
(7, 'Vokkaliga'),
(8, 'Brahmin'),
(9, 'Rajput'),
(10, 'Kuruba');

-- ============================================================
-- RELIGION MASTER
-- ============================================================
INSERT INTO religion_master (religion_id, religion_name) VALUES
(1, 'Hindu'),
(2, 'Muslim'),
(3, 'Christian'),
(4, 'Sikh'),
(5, 'Buddhist'),
(6, 'Jain');

-- ============================================================
-- OCCUPATION MASTER
-- ============================================================
INSERT INTO occupation_master (occupation_id, occupation_name) VALUES
(1, 'Government Employee'),
(2, 'Farmer'),
(3, 'Business'),
(4, 'Student'),
(5, 'Daily Wage Labourer'),
(6, 'Driver'),
(7, 'Teacher'),
(8, 'Doctor'),
(9, 'Lawyer'),
(10, 'Retired'),
(11, 'Homemaker'),
(12, 'Unemployed');

-- ============================================================
-- ACT
-- ============================================================
INSERT INTO act (act_code, act_description, short_name, active) VALUES
('IPC', 'Indian Penal Code, 1860', 'IPC', '1'),
('BNS', 'Bharatiya Nyaya Sanhita, 2023', 'BNS', '1'),
('NDPS', 'Narcotic Drugs and Psychotropic Substances Act, 1985', 'NDPS', '1'),
('POCSO', 'Protection of Children from Sexual Offences Act, 2012', 'POCSO', '1'),
('IT', 'Information Technology Act, 2000', 'IT Act', '1'),
('MV', 'Motor Vehicles Act, 1988', 'MV Act', '1'),
('ARMS', 'Arms Act, 1959', 'ARMS', '1'),
('SC/ST', 'Scheduled Castes and Scheduled Tribes (Prevention of Atrocities) Act, 1989', 'SC/ST Act', '1'),
('DP', 'Dowry Prohibition Act, 1961', 'DP Act', '1'),
('EA', 'Explosive Substances Act, 1908', 'EA', '1');

-- ============================================================
-- SECTIONS
-- ============================================================
INSERT INTO section (act_code, section_code, section_description, active) VALUES
-- IPC sections
('IPC', '147', 'Rioting', '1'),
('IPC', '148', 'Rioting, armed with deadly weapon', '1'),
('IPC', '302', 'Murder', '1'),
('IPC', '304', 'Culpable homicide not amounting to murder', '1'),
('IPC', '307', 'Attempt to murder', '1'),
('IPC', '323', 'Voluntarily causing hurt', '1'),
('IPC', '324', 'Voluntarily causing hurt by dangerous weapons or means', '1'),
('IPC', '354', 'Assault or criminal force to woman with intent to outrage her modesty', '1'),
('IPC', '376', 'Rape', '1'),
('IPC', '379', 'Theft', '1'),
('IPC', '380', 'Burglary', '1'),
('IPC', '384', 'Extortion', '1'),
('IPC', '392', 'Robbery', '1'),
('IPC', '395', 'Dacoity', '1'),
('IPC', '406', 'Criminal breach of trust', '1'),
('IPC', '420', 'Cheating and dishonestly inducing delivery of property', '1'),
('IPC', '498A', 'Cruelty by husband or relatives of husband', '1'),
('IPC', '506', 'Criminal intimidation', '1'),
-- BNS sections
('BNS', '61', 'Rioting', '1'),
('BNS', '100', 'Murder', '1'),
('BNS', '101', 'Culpable homicide', '1'),
('BNS', '115', 'Voluntarily causing hurt', '1'),
('BNS', '137', 'Robbery', '1'),
('BNS', '138', 'Dacoity', '1'),
('BNS', '140', 'Theft', '1'),
('BNS', '141', 'Burglary', '1'),
('BNS', '223', 'Extortion', '1'),
('BNS', '303', 'Cheating', '1'),
('BNS', '316', 'Criminal intimidation', '1'),
('BNS', '318', 'Defamation', '1'),
('BNS', '351', 'Assault', '1'),
('BNS', '352', 'Criminal force', '1'),
-- NDPS sections
('NDPS', '15', 'Punishment for contravention in relation to cannabis plant and cannabis', '1'),
('NDPS', '20', 'Punishment for contravention in relation to opium', '1'),
('NDPS', '21', 'Punishment for contravention in relation to prepared opium', '1'),
('NDPS', '22', 'Punishment for contravention in relation to psychotropic substances', '1'),
('NDPS', '29', 'Punishment for attempt to commit offences', '1'),
-- IT Act sections
('IT', '66', 'Computer related offences', '1'),
('IT', '66A', 'Sending offensive messages through communication services', '1'),
('IT', '66C', 'Identity theft', '1'),
('IT', '66D', 'Cheating by personation using computer resource', '1'),
('IT', '67', 'Publishing obscene material in electronic form', '1');

-- ============================================================
-- CRIME HEAD
-- ============================================================
INSERT INTO crime_head (crime_head_id, crime_group_name, active) VALUES
(1, 'Offences Against Body', '1'),
(2, 'Offences Against Property', '1'),
(3, 'Offences Against Women', '1'),
(4, 'Economic Offences', '1'),
(5, 'Offences Against State', '1'),
(6, 'Cyber Crime', '1'),
(7, 'Narcotic Offences', '1'),
(8, 'Traffic Offences', '1'),
(9, 'Explosive Offences', '1'),
(10, 'Arms Offences', '1');

-- ============================================================
-- CRIME SUB HEAD
-- ============================================================
INSERT INTO crime_sub_head (crime_sub_head_id, crime_head_id, crime_head_name, seq_id) VALUES
(1, 1, 'Murder', 1),
(2, 1, 'Culpable Homicide', 2),
(3, 1, 'Assault', 3),
(4, 1, 'Robbery', 4),
(5, 1, 'Dacoity', 5),
(6, 2, 'Burglary', 1),
(7, 2, 'Theft', 2),
(8, 2, 'Criminal Breach of Trust', 3),
(9, 2, 'Arson', 4),
(10, 3, 'Rape', 1),
(11, 3, 'Dowry Death', 2),
(12, 3, 'Cruelty by Husband', 3),
(13, 3, 'Molestation', 4),
(14, 4, 'Cheating', 1),
(15, 4, 'Fraud', 2),
(16, 4, 'Extortion', 3),
(17, 4, 'Forgery', 4),
(18, 6, 'Hacking', 1),
(19, 6, 'Online Fraud', 2),
(20, 6, 'Identity Theft', 3),
(21, 7, 'NDPS Act Violation', 1),
(22, 7, 'Possession', 2),
(23, 8, 'Hit and Run', 1),
(24, 8, 'DUI', 2),
(25, 10, 'Illegal Arms', 1),
(26, 9, 'Explosive Possession', 1),
(27, 5, 'Sedition', 1),
(28, 5, 'Terrorism', 2);

-- ============================================================
-- COURT
-- ============================================================
INSERT INTO court (court_id, court_name, district_id, state_id, active) VALUES
(1, 'Principal Civil Judge, Bengaluru Urban', 1, 1, '1'),
(2, 'Sessions Court, Bengaluru Urban', 1, 1, '1'),
(3, 'Chief Judicial Magistrate Court, Kalaburgi', 2, 1, '1'),
(4, 'Sessions Court, Kalaburgi', 2, 1, '1'),
(5, 'District Court, Mysuru', 3, 1, '1'),
(6, 'Sessions Court, Mangaluru', 4, 1, '1'),
(7, 'Civil Court, Hubballi', 5, 1, '1'),
(8, 'Sessions Court, Belagavi', 6, 1, '1'),
(9, 'Fast Track Court, Shivamogga', 7, 1, '1'),
(10, 'CJM Court, Ballari', 8, 1, '1');

-- ============================================================
-- CRIME HEAD ACT SECTION mapping
-- ============================================================
INSERT INTO crime_head_act_section (crime_head_id, act_code, section_code) VALUES
(1, 'IPC', '302'), (1, 'IPC', '304'), (1, 'IPC', '307'),
(1, 'BNS', '100'), (1, 'BNS', '101'),
(2, 'IPC', '379'), (2, 'IPC', '380'), (2, 'IPC', '392'),
(2, 'BNS', '140'), (2, 'BNS', '141'), (2, 'BNS', '137'),
(3, 'IPC', '354'), (3, 'IPC', '376'),
(4, 'IPC', '420'), (4, 'IPC', '406'),
(4, 'BNS', '303'), (4, 'BNS', '223'),
(6, 'IT', '66'), (6, 'IT', '66C'), (6, 'IT', '66D'),
(7, 'NDPS', '15'), (7, 'NDPS', '21'), (7, 'NDPS', '22');

-- ============================================================
-- EMPLOYEE
-- ============================================================
INSERT INTO employee (employee_id, district_id, unit_id, rank_id, designation_id, kgid, first_name, employee_dob, gender_id, blood_group_id, physically_challenged, appointment_date) VALUES
(1, 1, 1, 8, 4, 'KGID-10001', 'Basavaraj Patil', '1982-05-15', 1, 3, '0', '2008-07-20'),
(2, 1, 2, 6, 2, 'KGID-10002', 'Sunita Sharma', '1985-09-22', 2, 1, '0', '2011-03-15'),
(3, 2, 4, 9, 5, 'KGID-10003', 'Ramesh Kumar', '1988-11-10', 1, 5, '0', '2014-01-10'),
(4, 2, 5, 8, 4, 'KGID-10004', 'Venkatesh Gouda', '1980-03-28', 1, 2, '0', '2006-06-15'),
(5, 3, 7, 9, 5, 'KGID-10005', 'Manjula Devi', '1987-07-03', 2, 4, '0', '2013-08-01'),
(6, 1, 1, 10, 5, 'KGID-10006', 'Suresh Babu', '1990-12-18', 1, 3, '0', '2016-04-22'),
(7, 4, 10, 8, 4, 'KGID-10007', 'Prakash Naik', '1983-08-07', 1, 1, '0', '2009-09-12'),
(8, 2, 4, 7, 5, 'KGID-10008', 'Girish Desai', '1991-02-14', 1, 4, '0', '2017-02-01'),
(9, 5, 12, 9, 8, 'KGID-10009', 'Anil Kumar H', '1986-06-25', 1, 2, '0', '2012-11-15'),
(10, 6, 14, 8, 4, 'KGID-10010', 'Mahesh Jadhav', '1981-10-30', 1, 3, '0', '2007-05-20');

-- ============================================================
-- CASE MASTER (15 cases across Karnataka)
-- ============================================================
INSERT INTO case_master (case_master_id, crime_no, case_no, crime_registered_date, police_person_id, police_station_id, case_category_id, gravity_offence_id, crime_major_head_id, crime_minor_head_id, case_status_id, court_id, incident_from_date, incident_to_date, info_received_ps_date, latitude, longitude, brief_facts) VALUES
(1, '104430006202600001', '202600001', '2026-07-15', 1, 1, 1, 1, 2, 4, 1, 2, '2026-07-15 14:30:00', '2026-07-15 14:45:00', '2026-07-15 15:00:00', 12.9716, 77.5946, 'Armed robbery at a jewelry shop in Bengaluru. Two masked assailants threatened shopkeeper with knife and decamped with gold ornaments worth Rs. 5,00,000.'),
(2, '104430006202600002', '202600002', '2026-07-14', 6, 1, 1, 2, 2, 6, 2, 1, '2026-07-14 02:00:00', '2026-07-14 02:30:00', '2026-07-14 08:30:00', 12.9352, 77.6245, 'Burglary at residential apartment in Indiranagar. Entry through back window, electronics and cash stolen.'),
(3, '204430006202600003', '202600003', '2026-07-13', 3, 4, 1, 1, 4, 14, 3, 3, '2026-07-13 10:00:00', '2026-07-13 10:00:00', '2026-07-13 11:30:00', 17.3295, 76.8387, 'Online banking fraud involving transfer of Rs. 3,50,000 from victim account to accused account through phishing link.'),
(4, '104430006202600004', '202600004', '2026-07-12', 9, 12, 1, 4, 1, 3, 2, 7, '2026-07-12 18:00:00', '2026-07-12 18:15:00', '2026-07-12 18:30:00', 15.3647, 75.1240, 'Physical assault near Hubballi Metro station. Victim attacked by three persons over personal enmity.'),
(5, '104430006202600005', '202600005', '2026-07-11', 5, 7, 1, 2, 2, 7, 4, 5, '2026-07-11 12:00:00', '2026-07-11 12:30:00', '2026-07-11 13:00:00', 12.3052, 76.6551, 'Theft of mobile phone and wallet from market area in Mysuru. CCTV footage available.'),
(6, '104430006202600006', '202600006', '2026-07-10', 3, 4, 1, 1, 7, 21, 3, 4, '2026-07-10 20:00:00', '2026-07-10 20:00:00', '2026-07-10 20:45:00', 17.3350, 76.8375, 'Narcotics seized during routine traffic check in Kalaburgi. 200g ganja and 10g heroin recovered.'),
(7, '104430006202600007', '202600007', '2026-07-09', 1, 1, 1, 2, 2, 7, 1, 1, '2026-07-09 22:00:00', '2026-07-09 22:30:00', '2026-07-09 23:00:00', 12.9850, 77.5800, 'Vehicle theft from parking lot near Bengaluru Railway Station. White Maruti Swift stolen.'),
(8, '204430006202600008', '202600008', '2026-07-08', 4, 5, 1, 1, 2, 9, 2, 4, '2026-07-08 03:00:00', '2026-07-08 03:30:00', '2026-07-08 06:00:00', 17.3380, 76.8320, 'Arson at abandoned godown in Kalaburgi industrial area. Fire broke out at 3 AM, suspected deliberate.'),
(9, '104430006202600009', '202600009', '2026-07-07', 7, 10, 1, 1, 1, 4, 1, 6, '2026-07-07 16:00:00', '2026-07-07 16:20:00', '2026-07-07 17:00:00', 12.8730, 74.8800, 'Robbery at knife point near Mangaluru Central Market. Cash and mobile stolen.'),
(10, '104430006202600010', '202600010', '2026-07-06', 10, 14, 1, 1, 1, 5, 3, 8, '2026-07-06 11:00:00', '2026-07-06 11:30:00', '2026-07-06 12:00:00', 15.8584, 74.4985, 'Dacoity at a bank in Belagavi. Five armed persons looted Rs. 12,00,000.'),
(11, '204430006202600011', '202600011', '2026-07-05', 3, 4, 1, 1, 4, 15, 2, 3, '2026-07-05 09:00:00', '2026-07-05 09:00:00', '2026-07-05 10:30:00', 17.3250, 76.8390, 'Financial fraud of Rs. 8,00,000 through forged documents and fake investment scheme.'),
(12, '104430006202600012', '202600012', '2026-07-04', 5, 7, 1, 2, 3, 12, 4, 5, '2026-07-04 20:00:00', '2026-07-04 20:15:00', '2026-07-04 21:00:00', 12.3100, 76.6600, 'Dowry harassment and cruelty case in Mysuru. Victim tortured by husband and in-laws.'),
(13, '104430006202600013', '202600013', '2026-07-03', 4, 5, 1, 2, 6, 18, 2, 4, '2026-07-03 14:00:00', '2026-07-03 14:00:00', '2026-07-03 15:30:00', 17.3400, 76.8400, 'Hacking of corporate email server. Stolen data includes employee records of 500+ persons.'),
(14, '304430006202600014', '202600014', '2026-07-02', 8, 4, 1, 1, 1, 1, 1, 4, '2026-07-02 23:00:00', '2026-07-02 23:30:00', '2026-07-03 01:00:00', 17.3300, 76.8350, 'Murder case in Kalaburgi. Victim found dead with stab wounds near railway track.'),
(15, '104430006202600015', '202600015', '2026-07-01', 7, 10, 1, 4, 10, 25, 7, 6, '2026-07-01 15:00:00', '2026-07-01 15:00:00', '2026-07-01 16:00:00', 12.8700, 74.8800, 'Illegal firearm recovered during search operation. Country-made pistol and 5 rounds seized.');

-- ============================================================
-- INV_OCCURANCE_TIME
-- ============================================================
INSERT INTO inv_occurance_time (case_master_id, occurrence_from, occurrence_to, latitude, longitude) VALUES
(1, '2026-07-15 14:30:00', '2026-07-15 14:45:00', 12.9716, 77.5946),
(2, '2026-07-14 02:00:00', '2026-07-14 02:30:00', 12.9352, 77.6245),
(3, '2026-07-13 10:00:00', '2026-07-13 10:00:00', 17.3295, 76.8387),
(4, '2026-07-12 18:00:00', '2026-07-12 18:15:00', 15.3647, 75.1240),
(5, '2026-07-11 12:00:00', '2026-07-11 12:30:00', 12.3052, 76.6551),
(6, '2026-07-10 20:00:00', '2026-07-10 20:00:00', 17.3350, 76.8375),
(7, '2026-07-09 22:00:00', '2026-07-09 22:30:00', 12.9850, 77.5800),
(8, '2026-07-08 03:00:00', '2026-07-08 03:30:00', 17.3380, 76.8320),
(9, '2026-07-07 16:00:00', '2026-07-07 16:20:00', 12.8730, 74.8800),
(10, '2026-07-06 11:00:00', '2026-07-06 11:30:00', 15.8584, 74.4985),
(11, '2026-07-05 09:00:00', '2026-07-05 09:00:00', 17.3250, 76.8390),
(12, '2026-07-04 20:00:00', '2026-07-04 20:15:00', 12.3100, 76.6600),
(13, '2026-07-03 14:00:00', '2026-07-03 14:00:00', 17.3400, 76.8400),
(14, '2026-07-02 23:00:00', '2026-07-02 23:30:00', 17.3300, 76.8350),
(15, '2026-07-01 15:00:00', '2026-07-01 15:00:00', 12.8700, 74.8800);

-- ============================================================
-- COMPLAINANT DETAILS
-- ============================================================
INSERT INTO complainant_details (complainant_id, case_master_id, complainant_name, age_year, occupation_id, religion_id, caste_id, gender_id) VALUES
(1, 1, 'Lakshmi Iyer', 45, 3, 1, 7, 2),
(2, 2, 'Anand Raj', 34, 1, 1, 1, 1),
(3, 3, 'Vikram Singh', 52, 3, 2, 4, 1),
(4, 4, 'Pradeep Kumar', 28, 5, 1, 1, 1),
(5, 5, 'Meena Kumari', 38, 11, 1, 2, 2),
(6, 6, 'Inspector Ramesh Kumar', 38, 1, 1, 1, 1),
(7, 7, 'Sunil Verma', 41, 3, 4, 1, 1),
(8, 8, 'Inspector Ramesh Kumar', 38, 1, 1, 1, 1),
(9, 9, 'Mariya Dsouza', 30, 3, 3, 1, 2),
(10, 10, 'Suresh Hosamani', 55, 2, 1, 6, 1),
(11, 11, 'Priya Sharma', 33, 7, 1, 1, 2),
(12, 12, 'Kavitha R', 29, 11, 1, 1, 2),
(13, 13, 'Naveen Technologies Pvt Ltd', NULL, 3, 1, 1, 1),
(14, 14, 'Yellappa Gouda', 62, 2, 1, 6, 1),
(15, 15, 'Inspector Girish Desai', 35, 1, 1, 1, 1);

-- ============================================================
-- VICTIM
-- ============================================================
INSERT INTO victim (victim_master_id, case_master_id, victim_name, age_year, gender_id, victim_police) VALUES
(1, 1, 'Lakshmi Iyer', 45, 2, '0'),
(2, 2, 'Anand Raj', 34, 1, '0'),
(3, 3, 'Vikram Singh', 52, 1, '0'),
(4, 4, 'Pradeep Kumar', 28, 1, '0'),
(5, 5, 'Meena Kumari', 38, 2, '0'),
(6, 9, 'Mariya Dsouza', 30, 2, '0'),
(7, 10, 'Suresh Hosamani', 55, 1, '0'),
(8, 12, 'Kavitha R', 29, 2, '0'),
(9, 14, 'Yellappa Gouda', 62, 1, '0');

-- ============================================================
-- ACCUSED
-- ============================================================
INSERT INTO accused (accused_master_id, case_master_id, accused_name, age_year, gender_id, person_id) VALUES
(1, 1, 'Ravi Kumar', 28, 1, 'A1'),
(2, 1, 'Imran Sheikh', 32, 1, 'A2'),
(3, 2, 'Suresh Patil', 35, 1, 'A1'),
(4, 3, 'Vijay Singh', 40, 1, 'A1'),
(5, 4, 'Manoj Joshi', 30, 1, 'A1'),
(6, 5, 'Arjun Mehta', 22, 1, 'A1'),
(7, 6, 'Rajesh Verma', 38, 1, 'A1'),
(8, 8, 'Unknown Person', NULL, NULL, 'A1'),
(9, 9, 'Mohammed Irfan', 26, 1, 'A1'),
(10, 10, 'Gang of Five', NULL, NULL, 'A1'),
(11, 11, 'Kiran Desai', 45, 1, 'A1'),
(12, 12, 'Rajesh Kumar', 34, 1, 'A1'),
(13, 13, 'Farhan Sheikh', 29, 1, 'A1'),
(14, 14, 'Deepak Nair', 31, 1, 'A1'),
(15, 15, 'Unknown Person', NULL, NULL, 'A1');

-- ============================================================
-- ACT SECTION ASSOCIATION
-- ============================================================
INSERT INTO act_section_association (case_master_id, act_code, section_code, act_order_id, section_order_id) VALUES
(1, 'IPC', '392', 1, 1), (1, 'IPC', '397', 1, 2), (1, 'IPC', '506', 1, 3),
(2, 'IPC', '380', 1, 1), (2, 'IPC', '457', 1, 2),
(3, 'IT', '66D', 1, 1), (3, 'IT', '66C', 1, 2), (3, 'IPC', '420', 2, 1),
(4, 'IPC', '323', 1, 1), (4, 'IPC', '324', 1, 2), (4, 'IPC', '506', 1, 3),
(5, 'IPC', '379', 1, 1),
(6, 'NDPS', '20', 1, 1), (6, 'NDPS', '21', 1, 2),
(7, 'IPC', '379', 1, 1), (7, 'IPC', '34', 1, 2),
(8, 'IPC', '436', 1, 1), (8, 'IPC', '435', 1, 2),
(9, 'IPC', '392', 1, 1), (9, 'IPC', '397', 1, 2),
(10, 'IPC', '395', 1, 1), (10, 'IPC', '397', 1, 2), (10, 'IPC', '34', 1, 3),
(11, 'IPC', '420', 1, 1), (11, 'IPC', '406', 1, 2), (11, 'IPC', '34', 1, 3),
(12, 'IPC', '498A', 1, 1), (12, 'DP', '304B', 2, 1),
(13, 'IT', '66', 1, 1), (13, 'IT', '43', 1, 2),
(14, 'IPC', '302', 1, 1), (14, 'IPC', '34', 1, 2),
(15, 'ARMS', '25', 1, 1), (15, 'ARMS', '27', 1, 2);

-- ============================================================
-- ARREST / SURRENDER
-- ============================================================
INSERT INTO arrest_surrender (arrest_surrender_id, case_master_id, arrest_surrender_type_id, arrest_surrender_date, arrest_surrender_state_id, arrest_surrender_district_id, police_station_id, io_id, court_id) VALUES
(1, 1, 1, '2026-07-16', 1, 1, 1, 6, 2),
(2, 2, 1, '2026-07-15', 1, 1, 1, 6, 1),
(3, 3, 1, '2026-07-14', 1, 2, 4, 3, 3),
(4, 4, 2, '2026-07-13', 1, 5, 12, 9, 7),
(5, 5, 1, '2026-07-12', 1, 3, 7, 5, 5),
(6, 6, 1, '2026-07-11', 1, 2, 4, 3, 4),
(7, 9, 1, '2026-07-08', 1, 4, 10, 7, 6),
(8, 10, 1, '2026-07-09', 1, 6, 14, 10, 8),
(9, 11, 2, '2026-07-08', 1, 2, 4, 8, 3),
(10, 12, 1, '2026-07-06', 1, 3, 7, 5, 5),
(11, 13, 1, '2026-07-05', 1, 2, 4, 8, 4),
(12, 14, 1, '2026-07-04', 1, 2, 4, 3, 4);

-- ============================================================
-- INV_ARREST_SURRENDER_ACCUSED (Junction)
-- ============================================================
INSERT INTO inv_arrest_surrender_accused (arrest_surrender_id, accused_master_id, is_accused, is_complainant_accused) VALUES
(1, 1, '1', '0'), (1, 2, '1', '0'),
(2, 3, '1', '0'),
(3, 4, '1', '0'),
(4, 5, '1', '0'),
(5, 6, '1', '0'),
(6, 7, '1', '0'),
(7, 9, '1', '0'),
(8, 10, '1', '0'),
(9, 11, '1', '0'),
(10, 12, '1', '0'),
(11, 13, '1', '0'),
(12, 14, '1', '0');

-- ============================================================
-- CHARGESHEET DETAILS
-- ============================================================
INSERT INTO chargesheet_details (cs_id, case_master_id, cs_date, cs_type, police_person_id) VALUES
(1, 3, '2026-07-18', 'A', 3),
(2, 5, '2026-07-16', 'A', 5),
(3, 6, '2026-07-15', 'A', 3),
(4, 10, '2026-07-12', 'A', 10),
(5, 12, '2026-07-08', 'A', 5),
(6, 15, '2026-07-04', 'B', 10);

-- ============================================================
-- FINANCIAL TRANSACTIONS (linked to cases)
-- ============================================================
INSERT INTO financial_transactions (transaction_ref, sender_account_id, recipient_account_id, amount, currency, transaction_date, transaction_type, is_flagged, flag_reason, risk_score, related_case_id) VALUES
('TXN-2026-001', 'ACC-1001', 'ACC-2001', 75000.00, 'INR', '2026-07-15 05:00:00+05:30', 'WIRE', true, 'High-value wire transfer to known tax haven', 8.5, 1),
('TXN-2026-002', 'ACC-1002', 'ACC-2002', 120000.00, 'INR', '2026-07-15 09:15:00+05:30', 'CASH_DEPOSIT', true, 'Cash deposit exceeding reporting threshold', 7.2, 1),
('TXN-2026-003', 'ACC-1001', 'ACC-2003', 50000.00, 'INR', '2026-07-16 03:45:00+05:30', 'WIRE', true, 'Linked to previously flagged account', 6.8, 1),
('TXN-2026-004', 'ACC-1003', 'ACC-2004', 350000.00, 'INR', '2026-07-13 06:00:00+05:30', 'TRANSFER', true, 'Phishing fraud transfer', 9.2, 3),
('TXN-2026-005', 'ACC-1004', 'ACC-2005', 95000.00, 'INR', '2026-07-16 10:50:00+05:30', 'CASH_WITHDRAWAL', true, 'Unusual pattern of large withdrawals', 9.1, 6),
('TXN-2026-006', 'ACC-1005', 'ACC-2006', 200000.00, 'INR', '2026-07-17 02:30:00+05:30', 'CRYPTO', true, 'High-value crypto purchase linked to suspect', 8.8, 9),
('TXN-2026-007', 'ACC-1002', 'ACC-2007', 8000.00, 'INR', '2026-07-17 08:00:00+05:30', 'CASH_DEPOSIT', false, NULL, 1.5, 3),
('TXN-2026-008', 'ACC-1006', 'ACC-2008', 300000.00, 'INR', '2026-07-18 04:30:00+05:30', 'WIRE', true, 'Multiple rapid high-value transfers from shell company', 9.5, 11),
('TXN-2026-009', 'ACC-1007', 'ACC-2009', 180000.00, 'INR', '2026-07-05 11:00:00+05:30', 'TRANSFER', true, 'Fraudulent investment scheme transfer', 8.9, 11),
('TXN-2026-010', 'ACC-1008', 'ACC-2010', 45000.00, 'INR', '2026-07-10 14:20:00+05:30', 'WIRE', false, NULL, 2.0, 8);

-- ============================================================
-- PROPERTY RECORDS
-- ============================================================
INSERT INTO property_records (case_master_id, property_type, description, estimated_value, serial_number, is_recovered) VALUES
(1, 'JEWELRY', 'Gold necklace and diamond earrings', 350000.00, NULL, false),
(1, 'JEWELRY', 'Gold chain and mangalsutra', 150000.00, NULL, false),
(2, 'ELECTRONICS', 'Samsung 65-inch Smart TV', 85000.00, 'TV-SAM-65-001', false),
(2, 'ELECTRONICS', 'MacBook Pro 14-inch', 180000.00, 'MBP-APL-001', false),
(3, 'CASH', 'Bank transfer fraud amount', 350000.00, NULL, true),
(5, 'ELECTRONICS', 'iPhone 15 Pro and AirPods', 135000.00, 'APL-IP15P-789', false),
(6, 'CASH', 'Seized narcotics cash', 45000.00, NULL, true),
(7, 'VEHICLE', 'White Maruti Swift Dzire', 650000.00, 'KA-01-AB-1234', false),
(9, 'ELECTRONICS', 'Laptop and two mobile phones', 95000.00, NULL, false),
(10, 'CASH', 'Bank cash looted', 1200000.00, NULL, false),
(15, 'WEAPON', 'Country-made pistol', 0.00, NULL, true);
