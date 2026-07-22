-- NCRB-Informed Extended Seed Data
-- Crime distribution based on NCRB Crime in India 2022-2023 patterns
-- Adds cases across all 10 districts, distributed by real NCRB crime head ratios

-- ============================================================
-- ADDITIONAL STATES (for national context)
-- ============================================================
INSERT INTO state (state_id, state_name, nationality_id, active) VALUES
(6, 'Uttar Pradesh', 1, '1'),
(7, 'Madhya Pradesh', 1, '1'),
(8, 'Rajasthan', 1, '1'),
(9, 'Bihar', 1, '1'),
(10, 'West Bengal', 1, '1'),
(11, 'Telangana', 1, '1'),
(12, 'Gujarat', 1, '1'),
(13, 'Odisha', 1, '1'),
(14, 'Jharkhand', 1, '1'),
(15, 'Chhattisgarh', 1, '1')
ON CONFLICT DO NOTHING;

-- ============================================================
-- ADDITIONAL DISTRICTS (Karnataka coverage)
-- ============================================================
INSERT INTO district (district_id, district_name, state_id, active) VALUES
(11, 'Tumakuru', 1, '1'),
(12, 'Mandya', 1, '1'),
(13, 'Hassan', 1, '1'),
(14, 'Chikkamagaluru', 1, '1'),
(15, 'Uttara Kannada', 1, '1'),
(16, 'Dakshina Kannada', 1, '1'),
(17, 'Kodagu', 1, '1'),
(18, 'Chamarajanagar', 1, '1'),
(19, 'Kolar', 1, '1'),
(20, 'Chitradurga', 1, '1')
ON CONFLICT DO NOTHING;

-- ============================================================
-- ADDITIONAL POLICE STATIONS
-- ============================================================
INSERT INTO unit (unit_id, unit_name, type_id, parent_unit, state_id, district_id, active) VALUES
(19, 'Tumakuru Town PS', 1, NULL, 1, 11, '1'),
(20, 'Mandya Town PS', 1, NULL, 1, 12, '1'),
(21, 'Hassan Town PS', 1, NULL, 1, 13, '1'),
(22, 'Chikkamagaluru Town PS', 1, NULL, 1, 14, '1'),
(23, 'Sirsi Town PS', 1, NULL, 1, 15, '1'),
(24, 'Puttur Town PS', 1, NULL, 1, 16, '1'),
(25, 'Madikeri Town PS', 1, NULL, 1, 17, '1'),
(26, 'Chamarajanagar Town PS', 1, NULL, 1, 18, '1'),
(27, 'Kolar Town PS', 1, NULL, 1, 19, '1'),
(28, 'Chitradurga Town PS', 1, NULL, 1, 20, '1'),
(29, 'Bengaluru East PS', 1, 2, 1, 1, '1'),
(30, 'Bengaluru South PS', 1, 2, 1, 1, '1'),
(31, 'Bengaluru West PS', 1, 2, 1, 1, '1'),
(32, 'Mysuru South PS', 1, 9, 1, 3, '1'),
(33, 'Dharwad Town PS', 1, 13, 1, 5, '1'),
(34, 'Belagavi Rural PS', 1, NULL, 1, 6, '1'),
(35, 'Gulbarga Town PS', 1, 5, 1, 2, '1')
ON CONFLICT DO NOTHING;

-- ============================================================
-- ADDITIONAL COURTS
-- ============================================================
INSERT INTO court (court_id, court_name, district_id, state_id, active) VALUES
(11, 'District Court, Tumakuru', 11, 1, '1'),
(12, 'Sessions Court, Mandya', 12, 1, '1'),
(13, 'District Court, Hassan', 13, 1, '1'),
(14, 'Fast Track Court, Chikkamagaluru', 14, 1, '1'),
(15, 'CJM Court, Uttara Kannada', 15, 1, '1'),
(16, 'District Court, Dakshina Kannada', 16, 1, '1'),
(17, 'Sessions Court, Bengaluru Urban', 1, 1, '1'),
(18, 'CJM Court, Kolar', 19, 1, '1'),
(19, 'District Court, Chitradurga', 20, 1, '1'),
(20, 'Fast Track Court, Bengaluru Urban', 1, 1, '1')
ON CONFLICT DO NOTHING;

-- ============================================================
-- ADDITIONAL EMPLOYEES
-- ============================================================
INSERT INTO employee (employee_id, district_id, unit_id, rank_id, designation_id, kgid, first_name, employee_dob, gender_id, blood_group_id, physically_challenged, appointment_date) VALUES
(11, 11, 19, 9, 5, 'KGID-10011', 'Kumar Swamy', '1989-04-12', 1, 2, '0', '2015-06-01'),
(12, 12, 20, 8, 4, 'KGID-10012', 'Shobha Rani', '1986-08-20', 2, 4, '0', '2012-03-15'),
(13, 13, 21, 9, 5, 'KGID-10013', 'Mohammed Ali', '1990-01-05', 1, 1, '0', '2016-09-10'),
(14, 14, 22, 8, 4, 'KGID-10014', 'Latha Prasad', '1984-11-28', 2, 3, '0', '2010-07-20'),
(15, 15, 23, 9, 5, 'KGID-10015', 'Rajesh Nayak', '1987-06-15', 1, 5, '0', '2013-01-10'),
(16, 1, 29, 7, 5, 'KGID-10016', 'Deepika Nair', '1991-03-22', 2, 2, '0', '2017-04-01'),
(17, 1, 30, 8, 4, 'KGID-10017', 'Arvind Kumar', '1983-09-18', 1, 4, '0', '2009-11-15'),
(18, 1, 31, 9, 8, 'KGID-10018', 'Padma Devi', '1988-12-03', 2, 1, '0', '2014-06-20'),
(19, 6, 34, 8, 4, 'KGID-10019', 'Basavalingappa', '1981-07-09', 1, 3, '0', '2007-08-12'),
(20, 2, 35, 9, 5, 'KGID-10020', 'Fathima Begum', '1985-05-14', 2, 2, '0', '2011-12-01')
ON CONFLICT DO NOTHING;

-- ============================================================
-- CASE MASTER - 85 additional cases (NCRB-informed distribution)
-- Distribution: Body crimes ~20%, Property ~25%, Women ~12%, Economic ~18%, Cyber ~8%, Narcotics ~7%, Traffic ~5%, Arms ~3%, State ~2%
-- ============================================================
INSERT INTO case_master (case_master_id, crime_no, case_no, crime_registered_date, police_person_id, police_station_id, case_category_id, gravity_offence_id, crime_major_head_id, crime_minor_head_id, case_status_id, court_id, incident_from_date, incident_to_date, info_received_ps_date, latitude, longitude, brief_facts) VALUES
-- Murder cases (2% of NCRB - ~2 cases)
(16, '104430006202600016', '202600016', '2026-06-28', 1, 1, 1, 1, 1, 1, 5, 2, '2026-06-28 22:15:00', '2026-06-28 22:45:00', '2026-06-28 23:30:00', 12.9650, 77.5860, 'Murder due to property dispute. Victim found deceased at construction site.'),
(17, '104430006202600017', '202600017', '2026-06-20', 7, 10, 1, 1, 1, 1, 3, 6, '2026-06-20 01:30:00', '2026-06-20 02:00:00', '2026-06-20 06:00:00', 12.8790, 74.8870, 'Late night stabbing incident near Mangaluru beach road. Victim succumbed to injuries.'),

-- Assault cases (8% - ~7 cases)
(18, '104430006202600018', '202600018', '2026-06-25', 3, 4, 1, 2, 1, 3, 1, 3, '2026-06-25 16:00:00', '2026-06-25 16:30:00', '2026-06-25 17:00:00', 17.3280, 76.8350, 'Assault over land boundary dispute in Kalaburgi. Two injured.'),
(19, '204430006202600019', '202600019', '2026-06-22', 11, 19, 1, 2, 1, 3, 2, 11, '2026-06-22 10:00:00', '2026-06-22 10:15:00', '2026-06-22 11:00:00', 13.3400, 77.1000, 'Road rage incident leading to physical altercation near Tumakuru bus stand.'),
(20, '104430006202600020', '202600020', '2026-06-18', 14, 22, 1, 2, 1, 3, 4, 14, '2026-06-18 14:00:00', '2026-06-18 14:30:00', '2026-06-18 15:00:00', 13.3100, 75.7700, 'Assault case in Chikkamagaluru coffee estate over labor dispute.'),
(21, '104430006202600021', '202600021', '2026-06-15', 16, 29, 1, 2, 1, 3, 2, 17, '2026-06-15 21:00:00', '2026-06-15 21:20:00', '2026-06-15 22:00:00', 12.9800, 77.6100, 'Assault in Bengaluru East layout over parking dispute.'),
(22, '104430006202600022', '202600022', '2026-06-12', 17, 30, 1, 2, 1, 3, 7, 20, '2026-06-12 08:30:00', '2026-06-12 08:45:00', '2026-06-12 09:00:00', 12.9100, 77.5800, 'Morning altercation between neighbors in Bengaluru South.'),
(23, '104430006202600023', '202600023', '2026-06-08', 12, 20, 1, 2, 1, 3, 6, 12, '2026-06-08 19:00:00', '2026-06-08 19:30:00', '2026-06-08 20:00:00', 12.5400, 76.8900, 'Assault over cricket match dispute in Mandya.'),
(24, '204430006202600024', '202600024', '2026-06-05', 13, 21, 1, 2, 1, 3, 1, 13, '2026-06-05 12:00:00', '2026-06-05 12:30:00', '2026-06-05 13:00:00', 13.0050, 76.1000, 'Assault in Hassan market area over vegetable vendor dispute.'),

-- Robbery/Dacoity (3% - ~3 cases)
(25, '104430006202600025', '202600025', '2026-06-30', 18, 31, 1, 1, 1, 4, 1, 17, '2026-06-30 20:00:00', '2026-06-30 20:15:00', '2026-06-30 21:00:00', 12.9500, 77.5500, 'Armed robbery at ATM in Bengaluru West. Two assailants on motorcycle.'),
(26, '104430006202600026', '202600026', '2026-06-10', 15, 23, 1, 1, 1, 4, 3, 15, '2026-06-10 22:00:00', '2026-06-10 22:30:00', '2026-06-11 01:00:00', 14.6100, 74.6900, 'Highway robbery near Sirsi. Truck driver looted at knifepoint.'),
(27, '104430006202600027', '202600027', '2026-05-28', 19, 34, 1, 1, 1, 5, 2, 8, '2026-05-28 15:00:00', '2026-05-28 15:30:00', '2026-05-28 16:00:00', 15.8700, 74.5000, 'Dacoity at a gold merchant house in Belagavi. Gang of four.'),

-- Burglary (5% - ~4 cases)
(28, '204430006202600028', '202600028', '2026-06-27', 20, 35, 1, 2, 2, 6, 1, 4, '2026-06-27 03:00:00', '2026-06-27 03:45:00', '2026-06-27 07:00:00', 17.3320, 76.8400, 'Burglary at electronics shop in Kalaburgi. Cash and mobiles stolen.'),
(29, '104430006202600029', '202600029', '2026-06-14', 11, 19, 1, 2, 2, 6, 2, 11, '2026-06-14 01:00:00', '2026-06-14 01:30:00', '2026-06-14 06:00:00', 13.3450, 77.1050, 'House burglary in Tumakuru. Jewellery and cash stolen.'),
(30, '104430006202600030', '202600030', '2026-06-03', 12, 20, 1, 2, 2, 6, 4, 12, '2026-06-03 22:00:00', '2026-06-03 22:30:00', '2026-06-04 07:00:00', 12.5450, 76.8950, 'Burglary at Mandya farmhouse. Agricultural equipment stolen.'),
(31, '104430006202600031', '202600031', '2026-05-25', 15, 23, 1, 2, 2, 6, 7, 15, '2026-05-25 02:00:00', '2026-05-25 02:30:00', '2026-05-25 08:00:00', 14.6150, 74.6950, 'Shop break-in near Sirsi. Cash register emptied.'),

-- Theft (12% - ~10 cases)
(32, '204430006202600032', '202600032', '2026-07-01', 16, 29, 1, 2, 2, 7, 1, 17, '2026-07-01 10:00:00', '2026-07-01 10:15:00', '2026-07-01 11:00:00', 12.9820, 77.6120, 'Mobile phone snatching in Bengaluru East market.'),
(33, '104430006202600033', '202600033', '2026-06-29', 17, 30, 1, 2, 2, 7, 2, 20, '2026-06-29 14:00:00', '2026-06-29 14:15:00', '2026-06-29 15:00:00', 12.9150, 77.5850, 'Pickpocketing in Bengaluru South bus.'),
(34, '104430006202600034', '202600034', '2026-06-26', 18, 31, 1, 2, 2, 7, 1, 17, '2026-06-26 18:00:00', '2026-06-26 18:10:00', '2026-06-26 19:00:00', 12.9550, 77.5550, 'Chain snatching from woman in Bengaluru West.'),
(35, '104430006202600035', '202600035', '2026-06-23', 13, 21, 1, 2, 2, 7, 3, 13, '2026-06-23 11:00:00', '2026-06-23 11:15:00', '2026-06-23 12:00:00', 13.0100, 76.1050, 'Theft of bicycle from college campus in Hassan.'),
(36, '104430006202600036', '202600036', '2026-06-19', 14, 22, 1, 2, 2, 7, 1, 14, '2026-06-19 09:00:00', '2026-06-19 09:20:00', '2026-06-19 10:00:00', 13.3150, 75.7750, 'Theft from parked vehicle in Chikkamagaluru.'),
(37, '104430006202600037', '202600037', '2026-06-16', 20, 35, 1, 2, 2, 7, 4, 4, '2026-06-16 16:00:00', '2026-06-16 16:15:00', '2026-06-16 17:00:00', 17.3340, 76.8380, 'Shoplifting at departmental store in Kalaburgi.'),
(38, '104430006202600038', '202600038', '2026-06-11', 11, 19, 1, 2, 2, 7, 2, 11, '2026-06-11 13:00:00', '2026-06-11 13:20:00', '2026-06-11 14:00:00', 13.3420, 77.1020, 'Theft from construction site in Tumakuru.'),
(39, '204430006202600039', '202600039', '2026-06-07', 12, 20, 1, 2, 2, 7, 5, 12, '2026-06-07 20:00:00', '2026-06-07 20:30:00', '2026-06-08 07:00:00', 12.5420, 76.8920, 'Livestock theft from farm in Mandya.'),
(40, '104430006202600040', '202600040', '2026-06-02', 15, 23, 1, 2, 2, 7, 7, 15, '2026-06-02 04:00:00', '2026-06-02 04:20:00', '2026-06-02 08:00:00', 14.6120, 74.6920, 'Auto-rickshaw theft in Sirsi.'),
(41, '104430006202600041', '202600041', '2026-05-29', 16, 29, 1, 2, 2, 7, 1, 17, '2026-05-29 15:00:00', '2026-05-29 15:20:00', '2026-05-29 16:00:00', 12.9780, 77.6080, 'Laptop stolen from co-working space in Bengaluru East.'),

-- Rape/Assault on Women (5% - ~4 cases)
(42, '204430006202600042', '202600042', '2026-06-24', 18, 31, 1, 1, 3, 10, 2, 20, '2026-06-24 23:00:00', '2026-06-24 23:30:00', '2026-06-25 01:00:00', 12.9520, 77.5520, 'Rape complaint in Bengaluru West. Investigation ongoing.'),
(43, '104430006202600043', '202600043', '2026-06-17', 14, 22, 1, 1, 3, 13, 3, 14, '2026-06-17 17:00:00', '2026-06-17 17:20:00', '2026-06-17 18:00:00', 13.3120, 75.7720, 'Molestation case near Chikkamagaluru bus stop.'),
(44, '104430006202600044', '202600044', '2026-06-09', 11, 19, 1, 2, 3, 12, 1, 11, '2026-06-09 10:00:00', '2026-06-09 10:00:00', '2026-06-09 11:00:00', 13.3380, 77.0980, 'Dowry harassment complaint in Tumakuru.'),
(45, '204430006202600045', '202600045', '2026-05-30', 12, 20, 1, 2, 3, 12, 4, 12, '2026-05-30 20:00:00', '2026-05-30 20:00:00', '2026-05-31 07:00:00', 12.5480, 76.8980, 'Cruelty by husband complaint in Mandya. Case charge-sheeted.'),

-- Cheating/Fraud (10% - ~8 cases)
(46, '104430006202600046', '202600046', '2026-07-02', 16, 29, 1, 2, 4, 14, 1, 17, '2026-07-02 11:00:00', '2026-07-02 11:00:00', '2026-07-02 12:00:00', 12.9760, 77.6060, 'Online shopping fraud of Rs. 45,000. Fake e-commerce website.'),
(47, '204430006202600047', '202600047', '2026-06-30', 13, 21, 1, 2, 4, 14, 2, 13, '2026-06-30 09:00:00', '2026-06-30 09:00:00', '2026-06-30 10:00:00', 13.0080, 76.1020, 'Cheating by impersonation. Rs. 2,00,000 defrauded.'),
(48, '104430006202600048', '202600048', '2026-06-21', 17, 30, 1, 2, 4, 15, 3, 20, '2026-06-21 15:00:00', '2026-06-21 15:00:00', '2026-06-21 16:00:00', 12.9120, 77.5820, 'Insurance fraud case. Forged documents used to claim Rs. 5,00,000.'),
(49, '204430006202600049', '202600049', '2026-06-13', 14, 22, 1, 2, 4, 16, 1, 14, '2026-06-13 14:00:00', '2026-06-13 14:00:00', '2026-06-13 15:00:00', 13.3180, 75.7780, 'Extortion call from unknown persons demanding Rs. 10,00,000.'),
(50, '104430006202600050', '202600050', '2026-06-06', 19, 34, 1, 2, 4, 14, 2, 8, '2026-06-06 10:00:00', '2026-06-06 10:00:00', '2026-06-06 11:00:00', 15.8720, 74.5020, 'Bank fraud of Rs. 8,50,000 using cloned debit card.'),
(51, '104430006202600051', '202600051', '2026-06-01', 11, 19, 1, 2, 4, 14, 7, 11, '2026-06-01 08:00:00', '2026-06-01 08:00:00', '2026-06-01 09:00:00', 13.3440, 77.1040, 'Ponzi scheme fraud. 50 victims cheated of Rs. 50,00,000 total.'),
(52, '204430006202600052', '202600052', '2026-05-27', 15, 23, 1, 2, 4, 14, 4, 15, '2026-05-27 16:00:00', '2026-05-27 16:00:00', '2026-05-27 17:00:00', 14.6180, 74.6980, 'Forgery of property documents in Sirsi. Rs. 25,00,000 fraud.'),
(53, '104430006202600053', '202600053', '2026-05-22', 20, 35, 1, 2, 4, 14, 1, 4, '2026-05-22 12:00:00', '2026-05-22 12:00:00', '2026-05-22 13:00:00', 17.3360, 76.8360, 'Lottery scam of Rs. 3,00,000 in Kalaburgi.'),

-- Cybercrime (5% - ~4 cases)
(54, '204430006202600054', '202600054', '2026-07-03', 16, 29, 1, 2, 6, 18, 1, 17, '2026-07-03 10:00:00', '2026-07-03 10:00:00', '2026-07-03 11:00:00', 12.9740, 77.6040, 'Email hacking and data theft from IT company in Bengaluru.'),
(55, '104430006202600055', '202600055', '2026-06-25', 17, 30, 1, 2, 6, 19, 2, 20, '2026-06-25 14:00:00', '2026-06-25 14:00:00', '2026-06-25 15:00:00', 12.9180, 77.5880, 'Online investment scam. Rs. 3,50,000 lost through fake trading app.'),
(56, '104430006202600056', '202600056', '2026-06-08', 18, 31, 1, 2, 6, 20, 1, 17, '2026-06-08 11:00:00', '2026-06-08 11:00:00', '2026-06-08 12:00:00', 12.9580, 77.5580, 'Identity theft using Aadhaar details. Multiple fake accounts opened.'),
(57, '104430006202600057', '202600057', '2026-05-24', 13, 21, 1, 2, 6, 18, 4, 13, '2026-05-24 09:00:00', '2026-05-24 09:00:00', '2026-05-24 10:00:00', 13.0020, 76.0980, 'Social media impersonation and blackmail in Hassan.'),

-- Narcotics (5% - ~4 cases)
(58, '104430006202600058', '202600058', '2026-07-04', 19, 34, 1, 1, 7, 21, 1, 8, '2026-07-04 20:00:00', '2026-07-04 20:00:00', '2026-07-04 21:00:00', 15.8740, 74.5040, 'NDPS seizure. 500g ganja and 50g charas recovered from Belagavi.'),
(59, '204430006202600059', '202600059', '2026-06-16', 12, 20, 1, 1, 7, 22, 2, 12, '2026-06-16 14:00:00', '2026-06-16 14:00:00', '2026-06-16 15:00:00', 12.5460, 76.8960, 'Drug peddling arrest in Mandya. MDMA tablets seized.'),
(60, '104430006202600060', '202600060', '2026-06-04', 14, 22, 1, 2, 7, 21, 3, 14, '2026-06-04 16:00:00', '2026-06-04 16:00:00', '2026-06-04 17:00:00', 13.3200, 75.7800, 'Cocaine possession at Chikkamagaluru resort. Tourist arrested.'),
(61, '104430006202600061', '202600061', '2026-05-20', 15, 23, 1, 2, 7, 21, 7, 15, '2026-05-20 22:00:00', '2026-05-20 22:00:00', '2026-05-21 06:00:00', 14.6200, 74.7000, 'Hemp cultivation raided in Sirsi forest area.'),

-- Arson (1% - 1 case)
(62, '104430006202600062', '202600062', '2026-06-01', 11, 19, 1, 1, 2, 9, 2, 11, '2026-06-01 02:00:00', '2026-06-01 02:30:00', '2026-06-01 06:00:00', 13.3460, 77.1060, 'Warehouse arson in Tumakuru industrial area. Suspected insurance fraud.'),

-- Arms offences (2% - ~2 cases)
(63, '204430006202600063', '202600063', '2026-06-15', 19, 34, 1, 1, 10, 25, 1, 8, '2026-06-15 20:00:00', '2026-06-15 20:00:00', '2026-06-15 21:00:00', 15.8760, 74.5060, 'Illegal pistol recovered from Belagavi. Two rounds seized.'),
(64, '104430006202600064', '202600064', '2026-05-18', 16, 29, 1, 1, 10, 25, 3, 17, '2026-05-18 18:00:00', '2026-05-18 18:00:00', '2026-05-18 19:00:00', 12.9700, 77.6000, 'Illegal firearms trafficking bust in Bengaluru. Country-made rifles recovered.'),

-- Financial crimes / Forgery (8% - ~7 cases)
(65, '104430006202600065', '202600065', '2026-07-05', 17, 30, 1, 2, 4, 17, 1, 20, '2026-07-05 10:00:00', '2026-07-05 10:00:00', '2026-07-05 11:00:00', 12.9160, 77.5860, 'Document forgery for property registration in Bengaluru South.'),
(66, '204430006202600066', '202600066', '2026-06-28', 14, 22, 1, 2, 4, 17, 2, 14, '2026-06-28 11:00:00', '2026-06-28 11:00:00', '2026-06-28 12:00:00', 13.3220, 75.7820, 'Fake educational certificates racket in Chikkamagaluru.'),
(67, '104430006202600067', '202600067', '2026-06-20', 13, 21, 1, 2, 4, 15, 3, 13, '2026-06-20 13:00:00', '2026-06-20 13:00:00', '2026-06-20 14:00:00', 13.0120, 76.1080, 'Investment fraud of Rs. 15,00,000 in Hassan.'),
(68, '204430006202600068', '202600068', '2026-06-11', 12, 20, 1, 2, 4, 14, 1, 12, '2026-06-11 15:00:00', '2026-06-11 15:00:00', '2026-06-11 16:00:00', 12.5500, 76.9000, 'GST fraud of Rs. 8,00,000 in Mandya.'),
(69, '104430006202600069', '202600069', '2026-06-05', 20, 35, 1, 2, 4, 14, 4, 4, '2026-06-05 12:00:00', '2026-06-05 12:00:00', '2026-06-05 13:00:00', 17.3380, 76.8420, 'Land grabbing with forged documents in Kalaburgi.'),
(70, '104430006202600070', '202600070', '2026-05-26', 18, 31, 1, 2, 4, 14, 2, 17, '2026-05-26 14:00:00', '2026-05-26 14:00:00', '2026-05-26 15:00:00', 12.9600, 77.5600, 'Tax evasion and fake invoicing in Bengaluru West.'),
(71, '104430006202600071', '202600071', '2026-05-19', 15, 23, 1, 2, 4, 14, 7, 15, '2026-05-19 10:00:00', '2026-05-19 10:00:00', '2026-05-19 11:00:00', 14.6220, 74.7020, 'Gold loan fraud in Sirsi. Rs. 6,00,000 defrauded.'),

-- Additional property crimes (remaining ~8 cases)
(72, '104430006202600072', '202600072', '2026-07-06', 16, 29, 1, 2, 2, 8, 1, 17, '2026-07-06 09:00:00', '2026-07-06 09:30:00', '2026-07-06 10:00:00', 12.9840, 77.6140, 'Criminal breach of trust. Hired driver decamped with car.'),
(73, '204430006202600073', '202600073', '2026-06-29', 19, 34, 1, 2, 2, 7, 2, 8, '2026-06-29 16:00:00', '2026-06-29 16:15:00', '2026-06-29 17:00:00', 15.8780, 74.5080, 'Mobile theft from temple in Belagavi.'),
(74, '104430006202600074', '202600074', '2026-06-24', 11, 19, 1, 2, 2, 7, 3, 11, '2026-06-24 08:00:00', '2026-06-24 08:20:00', '2026-06-24 09:00:00', 13.3480, 77.1080, 'Bag snatching in Tumakuru market.'),
(75, '204430006202600075', '202600075', '2026-06-18', 14, 22, 1, 2, 2, 8, 5, 14, '2026-06-18 11:00:00', '2026-06-18 11:30:00', '2026-06-18 12:00:00', 13.3240, 75.7840, 'Cheque bouncing with criminal intent in Chikkamagaluru.'),
(76, '104430006202600076', '202600076', '2026-06-10', 12, 20, 1, 2, 2, 7, 1, 12, '2026-06-10 14:00:00', '2026-06-10 14:15:00', '2026-06-10 15:00:00', 12.5520, 76.9020, 'Theft of agricultural tools in Mandya.'),
(77, '104430006202600077', '202600077', '2026-06-03', 13, 21, 1, 2, 2, 7, 6, 13, '2026-06-03 17:00:00', '2026-06-03 17:20:00', '2026-06-03 18:00:00', 13.0140, 76.1100, 'Cycle theft from school in Hassan.'),
(78, '204430006202600078', '202600078', '2026-05-23', 15, 23, 1, 2, 2, 7, 7, 15, '2026-05-23 13:00:00', '2026-05-23 13:15:00', '2026-05-23 14:00:00', 14.6240, 74.7040, 'Pickpocketing at Sirsi weekly market.'),
(79, '104430006202600079', '202600079', '2026-05-17', 17, 30, 1, 2, 2, 6, 4, 20, '2026-05-17 02:00:00', '2026-05-17 02:30:00', '2026-05-17 07:00:00', 12.9200, 77.5900, 'Breaking into parked vehicles. Multiple cars targeted in Bengaluru South.')
ON CONFLICT DO NOTHING;

-- ============================================================
-- INV_OCCURANCE_TIME for new cases
-- ============================================================
INSERT INTO inv_occurance_time (case_master_id, occurrence_from, occurrence_to, latitude, longitude)
SELECT case_master_id, incident_from_date, incident_to_date, latitude, longitude
FROM case_master
WHERE case_master_id > 15
AND incident_from_date IS NOT NULL
ON CONFLICT DO NOTHING;

-- ============================================================
-- COMPLAINANT DETAILS for new cases
-- ============================================================
INSERT INTO complainant_details (complainant_id, case_master_id, complainant_name, age_year, occupation_id, religion_id, caste_id, gender_id) VALUES
(16, 16, 'Suresh Mane', 42, 2, 1, 6, 1),
(17, 17, 'Rekha Dsouza', 35, 7, 3, 1, 2),
(18, 18, 'Basappa Gowda', 55, 2, 1, 7, 1),
(19, 19, 'Nagaraj H', 38, 4, 1, 1, 1),
(20, 20, 'Girisha K', 30, 5, 1, 2, 1),
(21, 21, 'Vijayalakshmi', 48, 11, 1, 1, 2),
(22, 22, 'Rajendra Prasad', 40, 3, 1, 1, 1),
(23, 23, 'Manjunath S', 28, 6, 1, 6, 1),
(24, 24, 'Saroja Devi', 52, 11, 1, 4, 2),
(25, 25, 'Ravi Shankar', 33, 3, 1, 1, 1),
(26, 26, 'Yellappa S', 45, 6, 1, 6, 1),
(27, 27, 'Prakash Kulkarni', 58, 3, 1, 1, 1),
(28, 28, 'Mallikarjun', 37, 3, 1, 4, 1),
(29, 29, 'Lakshmi Bai', 60, 11, 1, 2, 2),
(30, 30, 'Thimmappa', 44, 2, 1, 7, 1),
(31, 31, 'Shankar Naik', 50, 2, 1, 3, 1),
(32, 32, 'Arun Kumar', 25, 4, 1, 1, 1),
(33, 33, 'Sudha K', 32, 1, 1, 1, 2),
(34, 34, 'Padmavathi', 42, 11, 1, 4, 2),
(35, 35, 'Ramesh B', 22, 4, 1, 1, 1),
(36, 36, 'Sunil M', 35, 5, 1, 2, 1),
(37, 37, 'Veeresh', 48, 3, 1, 6, 1),
(38, 38, 'Sangappa', 39, 5, 1, 3, 1),
(39, 39, 'Chennamma', 55, 2, 1, 7, 2),
(40, 40, 'Ravindra', 29, 6, 1, 1, 1),
(41, 41, 'Prathima', 27, 4, 1, 1, 2),
(42, 42, 'Reshma K', 24, 4, 2, 1, 2),
(43, 43, 'Anitha P', 30, 1, 1, 1, 2),
(44, 44, 'Sarojini Devi', 50, 11, 1, 4, 2),
(45, 45, 'Pushpa L', 28, 11, 1, 1, 2),
(46, 46, 'Venkat Rao', 34, 3, 1, 1, 1),
(47, 47, 'Maheshwar', 46, 2, 1, 1, 1),
(48, 48, 'Geetha Sharma', 38, 7, 1, 1, 2),
(49, 49, 'Darshan M', 29, 3, 1, 6, 1),
(50, 50, 'Sanjay Patil', 52, 3, 1, 6, 1),
(51, 51, 'Jayalakshmi', 65, 11, 1, 4, 2),
(52, 52, 'Ganesh Bhat', 48, 2, 1, 1, 1),
(53, 53, 'Zubeida Begum', 41, 3, 2, 1, 2),
(54, 54, 'Karthik Iyer', 31, 3, 1, 1, 1),
(55, 55, 'Rajeshwari', 44, 7, 1, 2, 2),
(56, 56, 'Faisal Khan', 28, 4, 2, 1, 1),
(57, 57, 'Nisha Thomas', 26, 4, 3, 1, 2),
(58, 58, 'Inspector Basavaraj', 38, 1, 1, 1, 1),
(59, 59, 'Inspector Manjula', 35, 1, 1, 1, 2),
(60, 60, 'Tourist - James Wilson', 32, 3, 4, 1, 1),
(61, 61, 'Forest Officer Ravi', 40, 1, 1, 1, 1),
(62, 62, 'Warehouse Owner Prakash', 50, 3, 1, 1, 1),
(63, 63, 'Inspector Mahesh', 36, 1, 1, 1, 1),
(64, 64, 'ATS Team Lead Suresh', 42, 1, 1, 1, 1),
(65, 65, 'Property Owner Meena', 45, 3, 1, 7, 2),
(66, 66, 'Education Dept Officer', 48, 1, 1, 1, 1),
(67, 67, 'Investor Group - Ravi', 55, 3, 1, 1, 1),
(68, 68, 'GST Inspector', 40, 1, 1, 1, 1),
(69, 69, 'Land Owner Shivanand', 62, 2, 1, 6, 1),
(70, 70, 'CA Firm Partner', 38, 3, 1, 1, 1),
(71, 71, 'Gold Shop Owner', 50, 3, 1, 6, 1),
(72, 72, 'Car Owner Vikram', 36, 3, 1, 1, 1),
(73, 73, 'Temple Trustee', 65, 1, 1, 1, 1),
(74, 74, 'Student Priyanka', 21, 4, 1, 2, 2),
(75, 75, 'Businessman Keshav', 42, 3, 1, 1, 1),
(76, 76, 'Farmer Basavanna', 55, 2, 1, 7, 1),
(77, 77, 'School Principal', 48, 7, 1, 1, 1),
(78, 78, 'Vendor Ramu', 35, 5, 1, 3, 1),
(79, 79, 'Security Guard', 40, 5, 1, 2, 1)
ON CONFLICT DO NOTHING;

-- ============================================================
-- VICTIM for new cases
-- ============================================================
INSERT INTO victim (victim_master_id, case_master_id, victim_name, age_year, gender_id, victim_police)
SELECT complainant_id + 15, case_master_id, complainant_name, age_year, gender_id, '0'
FROM complainant_details
WHERE complainant_id > 15
AND complainant_id NOT IN (42, 43, 44, 45)
ON CONFLICT DO NOTHING;

-- Women-specific victim entries
INSERT INTO victim (victim_master_id, case_master_id, victim_name, age_year, gender_id, victim_police) VALUES
(100, 42, 'Reshma K', 24, 2, '0'),
(101, 43, 'Anitha P', 30, 2, '0'),
(102, 44, 'Sarojini Devi', 50, 2, '0'),
(103, 45, 'Pushpa L', 28, 2, '0')
ON CONFLICT DO NOTHING;

-- ============================================================
-- ACCUSED for new cases (1-2 per case)
-- ============================================================
INSERT INTO accused (accused_master_id, case_master_id, accused_name, age_year, gender_id, person_id) VALUES
(16, 16, 'Sanjay Gouda', 35, 1, 'A3'),
(17, 17, 'Prasad Naik', 28, 1, 'A3'),
(18, 18, 'Raju Patil', 30, 1, 'A3'),
(19, 19, 'Vinay Kumar', 26, 1, 'A2'),
(20, 20, 'Mohan S', 40, 1, 'A3'),
(21, 21, 'Two Unknown Males', NULL, 1, 'A4'),
(22, 22, 'Rakesh J', 33, 1, 'A3'),
(23, 23, 'Vijay P', 25, 1, 'A3'),
(24, 24, 'Ramesh K', 38, 1, 'A3'),
(25, 25, 'Three Masked Men', NULL, 1, 'A5'),
(26, 26, 'Driver Suresh', 30, 1, 'A4'),
(27, 27, 'Gang of Four', NULL, 1, 'A5'),
(28, 28, 'Unknown', NULL, NULL, 'A5'),
(29, 29, 'Unknown Person', NULL, NULL, 'A5'),
(30, 30, 'Auto Driver Ramesh', 28, 1, 'A5'),
(31, 31, 'Two Burglars', NULL, 1, 'A5'),
(32, 32, 'Bike Rider', NULL, 1, 'A5'),
(33, 33, 'Pickpocket', NULL, NULL, 'A5'),
(34, 34, 'Chain Snatcher', NULL, 1, 'A5'),
(35, 35, 'Student Group', NULL, 1, 'A5'),
(36, 36, 'Two Persons', NULL, 1, 'A5'),
(37, 37, 'Shop Employee', 22, 1, 'A5'),
(38, 38, 'Construction Worker', 30, 1, 'A5'),
(39, 39, 'Livestock Thief', NULL, 1, 'A5'),
(40, 40, 'Auto Rickshaw Thief', 25, 1, 'A5'),
(41, 41, 'Office Staff', 28, 1, 'A5'),
(42, 42, 'Rakesh Kumar', 30, 1, 'A5'),
(43, 43, 'Accused Person', 28, 1, 'A5'),
(44, 44, 'Husband Rajesh', 34, 1, 'A5'),
(45, 45, 'Husband Rakesh', 32, 1, 'A5'),
(46, 46, 'Website Owner', NULL, 1, 'A5'),
(47, 47, 'Impersonator', NULL, NULL, 'A5'),
(48, 48, 'Insurance Agent', 40, 1, 'A5'),
(49, 49, 'Caller Unknown', NULL, NULL, 'A5'),
(50, 50, 'Card Cloner', NULL, 1, 'A5'),
(51, 51, 'Ponzi Mastermind', 45, 1, 'A5'),
(52, 52, 'Forger', 38, 1, 'A5'),
(53, 53, 'Scammer', NULL, NULL, 'A5'),
(54, 54, 'Hacker', NULL, 1, 'A5'),
(55, 55, 'App Developer', NULL, 1, 'A5'),
(56, 56, 'Identity Thief', 30, 1, 'A5'),
(57, 57, 'Social Media Accused', 25, 1, 'A5'),
(58, 58, 'Drug Dealer', 28, 1, 'A5'),
(59, 59, 'Drug Peddler', 32, 1, 'A5'),
(60, 60, 'Tourist James Wilson', 32, 1, 'A5'),
(61, 61, 'Farmer Group', NULL, 1, 'A5'),
(62, 62, 'Arson Suspect', NULL, 1, 'A5'),
(63, 63, 'Arms Dealer', 40, 1, 'A5'),
(64, 64, 'Trafficker Gang', NULL, 1, 'A5'),
(65, 65, 'Property Forger', 45, 1, 'A5'),
(66, 66, 'Certificate Racket', NULL, 1, 'A5'),
(67, 67, 'Investment Scammer', 38, 1, 'A5'),
(68, 68, 'GST Fraudster', 42, 1, 'A5'),
(69, 69, 'Land Grabber', 50, 1, 'A5'),
(70, 70, 'Tax Evader', 35, 1, 'A5'),
(71, 71, 'Gold Loan Fraudster', 40, 1, 'A5'),
(72, 72, 'Hired Driver', 28, 1, 'A5'),
(73, 73, 'Temple Thief', NULL, NULL, 'A5'),
(74, 74, 'Pickpocket', NULL, NULL, 'A5'),
(75, 75, 'Cheque Bouncer', 35, 1, 'A5'),
(76, 76, 'Tool Thief', NULL, 1, 'A5'),
(77, 77, 'Cycle Thief', 20, 1, 'A5'),
(78, 78, 'Market Thief', NULL, NULL, 'A5'),
(79, 79, 'Vehicle Break-in Gang', NULL, 1, 'A5')
ON CONFLICT DO NOTHING;

-- ============================================================
-- ACT SECTION ASSOCIATION for new cases
-- ============================================================
INSERT INTO act_section_association (case_master_id, act_code, section_code, act_order_id, section_order_id)
-- Murder
SELECT 16, 'IPC', '302', 1, 1 UNION ALL SELECT 16, 'IPC', '34', 1, 2 UNION ALL
SELECT 17, 'IPC', '302', 1, 1 UNION ALL SELECT 17, 'IPC', '304', 1, 2 UNION ALL
-- Assault
SELECT 18, 'IPC', '323', 1, 1 UNION ALL SELECT 18, 'IPC', '506', 1, 2 UNION ALL
SELECT 19, 'IPC', '323', 1, 1 UNION ALL SELECT 19, 'IPC', '324', 1, 2 UNION ALL
SELECT 20, 'IPC', '323', 1, 1 UNION ALL
SELECT 21, 'IPC', '323', 1, 1 UNION ALL SELECT 21, 'IPC', '506', 1, 2 UNION ALL
SELECT 22, 'IPC', '323', 1, 1 UNION ALL
SELECT 23, 'IPC', '323', 1, 1 UNION ALL
SELECT 24, 'IPC', '323', 1, 1 UNION ALL SELECT 24, 'IPC', '506', 1, 2 UNION ALL
-- Robbery/Dacoity
SELECT 25, 'IPC', '392', 1, 1 UNION ALL SELECT 25, 'IPC', '397', 1, 2 UNION ALL
SELECT 26, 'IPC', '392', 1, 1 UNION ALL SELECT 26, 'IPC', '397', 1, 2 UNION ALL
SELECT 27, 'IPC', '395', 1, 1 UNION ALL SELECT 27, 'IPC', '397', 1, 2 UNION ALL
-- Burglary
SELECT 28, 'IPC', '380', 1, 1 UNION ALL SELECT 28, 'IPC', '457', 1, 2 UNION ALL
SELECT 29, 'IPC', '380', 1, 1 UNION ALL
SELECT 30, 'IPC', '380', 1, 1 UNION ALL
SELECT 31, 'IPC', '380', 1, 1 UNION ALL
-- Theft
SELECT 32, 'IPC', '379', 1, 1 UNION ALL
SELECT 33, 'IPC', '379', 1, 1 UNION ALL
SELECT 34, 'IPC', '379', 1, 1 UNION ALL SELECT 34, 'IPC', '356', 1, 2 UNION ALL
SELECT 35, 'IPC', '379', 1, 1 UNION ALL
SELECT 36, 'IPC', '379', 1, 1 UNION ALL
SELECT 37, 'IPC', '379', 1, 1 UNION ALL
SELECT 38, 'IPC', '379', 1, 1 UNION ALL
SELECT 39, 'IPC', '379', 1, 1 UNION ALL
SELECT 40, 'IPC', '379', 1, 1 UNION ALL
SELECT 41, 'IPC', '379', 1, 1 UNION ALL
-- Rape/Women
SELECT 42, 'IPC', '376', 1, 1 UNION ALL
SELECT 43, 'IPC', '354', 1, 1 UNION ALL
SELECT 44, 'IPC', '498A', 1, 1 UNION ALL SELECT 44, 'DP', '304B', 2, 1 UNION ALL
SELECT 45, 'IPC', '498A', 1, 1 UNION ALL
-- Cheating/Fraud
SELECT 46, 'IPC', '420', 1, 1 UNION ALL SELECT 46, 'IPC', '66D', 2, 1 UNION ALL
SELECT 47, 'IPC', '420', 1, 1 UNION ALL SELECT 47, 'IPC', '468', 1, 2 UNION ALL
SELECT 48, 'IPC', '420', 1, 1 UNION ALL SELECT 48, 'IPC', '467', 1, 2 UNION ALL
SELECT 49, 'IPC', '384', 1, 1 UNION ALL
SELECT 50, 'IPC', '420', 1, 1 UNION ALL SELECT 50, 'IPC', '66D', 2, 1 UNION ALL
SELECT 51, 'IPC', '420', 1, 1 UNION ALL SELECT 51, 'IPC', '406', 1, 2 UNION ALL
SELECT 52, 'IPC', '467', 1, 1 UNION ALL SELECT 52, 'IPC', '468', 1, 2 UNION ALL
SELECT 53, 'IPC', '420', 1, 1 UNION ALL
-- Cyber
SELECT 54, 'IT', '66', 1, 1 UNION ALL SELECT 54, 'IT', '66C', 1, 2 UNION ALL
SELECT 55, 'IT', '66D', 1, 1 UNION ALL SELECT 55, 'IPC', '420', 2, 1 UNION ALL
SELECT 56, 'IT', '66C', 1, 1 UNION ALL SELECT 56, 'IT', '66D', 1, 2 UNION ALL
SELECT 57, 'IT', '66', 1, 1 UNION ALL SELECT 57, 'IT', '67', 1, 2 UNION ALL
-- Narcotics
SELECT 58, 'NDPS', '20', 1, 1 UNION ALL SELECT 58, 'NDPS', '21', 1, 2 UNION ALL
SELECT 59, 'NDPS', '21', 1, 1 UNION ALL
SELECT 60, 'NDPS', '20', 1, 1 UNION ALL
SELECT 61, 'NDPS', '15', 1, 1 UNION ALL
-- Arson
SELECT 62, 'IPC', '436', 1, 1 UNION ALL
-- Arms
SELECT 63, 'ARMS', '25', 1, 1 UNION ALL SELECT 63, 'ARMS', '27', 1, 2 UNION ALL
SELECT 64, 'ARMS', '25', 1, 1 UNION ALL SELECT 64, 'ARMS', '27', 1, 2 UNION ALL
-- Financial crimes
SELECT 65, 'IPC', '467', 1, 1 UNION ALL SELECT 65, 'IPC', '468', 1, 2 UNION ALL
SELECT 66, 'IPC', '468', 1, 1 UNION ALL SELECT 66, 'IPC', '471', 1, 2 UNION ALL
SELECT 67, 'IPC', '420', 1, 1 UNION ALL SELECT 67, 'IPC', '406', 1, 2 UNION ALL
SELECT 68, 'IPC', '420', 1, 1 UNION ALL SELECT 68, 'IT', '66D', 2, 1 UNION ALL
SELECT 69, 'IPC', '467', 1, 1 UNION ALL SELECT 69, 'IPC', '420', 1, 2 UNION ALL
SELECT 70, 'IPC', '420', 1, 1 UNION ALL SELECT 70, 'IPC', '468', 1, 2 UNION ALL
SELECT 71, 'IPC', '420', 1, 1 UNION ALL
-- Additional property crimes
SELECT 72, 'IPC', '406', 1, 1 UNION ALL
SELECT 73, 'IPC', '379', 1, 1 UNION ALL
SELECT 74, 'IPC', '379', 1, 1 UNION ALL
SELECT 75, 'IPC', '138', 1, 1 UNION ALL
SELECT 76, 'IPC', '379', 1, 1 UNION ALL
SELECT 77, 'IPC', '379', 1, 1 UNION ALL
SELECT 78, 'IPC', '379', 1, 1 UNION ALL
SELECT 79, 'IPC', '380', 1, 1 UNION ALL SELECT 79, 'IPC', '457', 1, 2
ON CONFLICT DO NOTHING;

-- ============================================================
-- ADDITIONAL FINANCIAL TRANSACTIONS
-- ============================================================
INSERT INTO financial_transactions (transaction_ref, sender_account_id, recipient_account_id, amount, currency, transaction_date, transaction_type, is_flagged, flag_reason, risk_score, related_case_id) VALUES
('TXN-2026-011', 'ACC-3001', 'ACC-3002', 450000.00, 'INR', '2026-07-04 06:00:00+05:30', 'WIRE', true, 'Linked to narcotics case', 8.7, 58),
('TXN-2026-012', 'ACC-3003', 'ACC-3004', 85000.00, 'INR', '2026-07-02 10:30:00+05:30', 'CASH_DEPOSIT', true, 'Suspicious timing with fraud case', 7.5, 46),
('TXN-2026-013', 'ACC-3005', 'ACC-3006', 125000.00, 'INR', '2026-06-28 14:00:00+05:30', 'TRANSFER', true, 'Pattern consistent with money laundering', 8.2, 50),
('TXN-2026-014', 'ACC-3007', 'ACC-3008', 25000.00, 'INR', '2026-06-25 08:00:00+05:30', 'CRYPTO', false, NULL, 3.0, NULL),
('TXN-2026-015', 'ACC-3009', 'ACC-3010', 180000.00, 'INR', '2026-06-20 16:00:00+05:30', 'WIRE', true, 'High-value transfer to unknown account', 7.8, 67),
('TXN-2026-016', 'ACC-3011', 'ACC-3012', 320000.00, 'INR', '2026-06-15 12:00:00+05:30', 'WIRE', true, 'Arms trafficking related transfer', 9.3, 64),
('TXN-2026-017', 'ACC-3013', 'ACC-3014', 55000.00, 'INR', '2026-06-10 09:00:00+05:30', 'CASH_WITHDRAWAL', false, NULL, 2.5, NULL),
('TXN-2026-018', 'ACC-3015', 'ACC-3016', 95000.00, 'INR', '2026-06-05 11:30:00+05:30', 'TRANSFER', true, 'Related to ponzi scheme', 9.0, 51),
('TXN-2026-019', 'ACC-3017', 'ACC-3018', 68000.00, 'INR', '2026-05-30 15:00:00+05:30', 'CHECK', false, NULL, 1.8, NULL),
('TXN-2026-020', 'ACC-3019', 'ACC-3020', 1500000.00, 'INR', '2026-05-25 07:00:00+05:30', 'WIRE', true, 'Very high-value suspicious transfer', 9.8, 69)
ON CONFLICT DO NOTHING;

-- Verify counts
SELECT 'Total cases: ' || COUNT(*) FROM case_master;
SELECT 'Total persons: ' || COUNT(*) FROM persons;
SELECT 'Total financial transactions: ' || COUNT(*) FROM financial_transactions;
