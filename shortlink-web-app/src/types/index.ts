export interface ShortLink {
  shortenedUrl: string;
  mainUrl: string;
}

export interface Pagination {
  total: number;
  page: number;
  limit: number;
}

export interface PaginatedResponse {
  data: ShortLink[];
  pagination: Pagination;
}