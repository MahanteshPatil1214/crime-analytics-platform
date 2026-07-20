export interface CaseMaster {
  caseMasterId: number;
  crimeNo: string;
  caseNo: string;
  crimeRegisteredDate: string;
  policePersonId: number | null;
  policeStationId: number | null;
  caseCategoryId: number | null;
  gravityOffenceId: number | null;
  crimeMajorHeadId: number | null;
  crimeMinorHeadId: number | null;
  caseStatusId: number | null;
  courtId: number | null;
  incidentFromDate: string | null;
  incidentToDate: string | null;
  infoReceivedPsDate: string | null;
  latitude: number | null;
  longitude: number | null;
  briefFacts: string | null;
}

export interface CaseDetail {
  case: CaseMaster;
  complainants: Complainant[];
  victims: Victim[];
  accused: Accused[];
  arrests: Arrest[];
  actSections: ActSection[];
  chargesheets: Chargesheet[];
  occurrenceTime: OccurrenceTime | null;
}

export interface Complainant {
  complainantId: number;
  caseMasterId: number;
  complainantName: string;
  ageYear: number | null;
  genderId: number | null;
}

export interface Victim {
  victimMasterId: number;
  caseMasterId: number;
  victimName: string;
  ageYear: number | null;
  genderId: number | null;
}

export interface Accused {
  accusedMasterId: number;
  caseMasterId: number;
  accusedName: string;
  ageYear: number | null;
  genderId: number | null;
  personId: string | null;
}

export interface Arrest {
  arrestSurrenderId: number;
  caseMasterId: number;
  arrestSurrenderTypeId: number | null;
  arrestSurrenderDate: string | null;
  ioId: number | null;
  courtId: number | null;
}

export interface ActSection {
  caseMasterId: number;
  actCode: string;
  sectionCode: string;
}

export interface Chargesheet {
  csId: number;
  caseMasterId: number;
  csDate: string | null;
  csType: string;
}

export interface OccurrenceTime {
  caseMasterId: number;
  occurrenceFrom: string | null;
  occurrenceTo: string | null;
  latitude: number | null;
  longitude: number | null;
}

export interface Involvement {
  type: string;
  name: string;
  age: number | null;
  genderId: number | null;
  personId?: string;
}

export interface CaseSearchResult {
  caseMasterId: number;
  crimeNo: string;
  caseNo: string;
  crimeRegisteredDate: string;
  briefFacts: string;
  latitude: number | null;
  longitude: number | null;
  caseStatusId: number;
  statusName: string;
  crimeMajorHeadId: number;
  crimeHeadName: string;
  policeStationId: number;
  policeStationName: string;
  districtId: number;
  districtName: string;
  accusedNames: string[];
  victimNames: string[];
  complainantNames: string[];
  actSections: string[];
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
