// ===============================
// GrowEasy CRM Record
// ===============================

export interface CrmRecord {
  created_at: string;

  name: string;

  email: string;

  country_code: string;

  mobile_without_country_code: string;

  company: string;

  city: string;

  state: string;

  country: string;

  lead_owner: string;

  crm_status:
    | "GOOD_LEAD_FOLLOW_UP"
    | "DID_NOT_CONNECT"
    | "BAD_LEAD"
    | "SALE_DONE"
    | "";

  crm_note: string;

  data_source:
    | "leads_on_demand"
    | "meridian_tower"
    | "eden_park"
    | "varah_swamy"
    | "sarjapur_plots"
    | "";

  possession_time: string;

  description: string;
}

// ===============================
// AI Response
// ===============================

export interface AiExtractionResponse {
  records: CrmRecord[];

  skipped: number;
}