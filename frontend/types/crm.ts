export type CrmStatus =
  | "GOOD_LEAD_FOLLOW_UP"
  | "DID_NOT_CONNECT"
  | "BAD_LEAD"
  | "SALE_DONE"
  | "";

export type DataSource =
  | "leads_on_demand"
  | "meridian_tower"
  | "eden_park"
  | "varah_swamy"
  | "sarjapur_plots"
  | "";

export interface CrmRecord {
  lead_id?: string;
  original_row?: number;
  created_at?: string;
  name: string;
  email: string;
  phone?: string;
  country_code: string;
  mobile_without_country_code: string;
  company: string;
  job_title?: string;
  industry?: string;
  website?: string;
  city?: string;
  state?: string;
  country?: string;
  lead_owner?: string;
  crm_status: CrmStatus;
  priority?: string;
  ai_insight?: string;
  recommended_action?: string;
  ai_reason?: string;
  crm_note?: string;
  data_source?: DataSource;
  possession_time?: string;
  description?: string;
  quality_score?: number;
  validation_issues?: string;
}
