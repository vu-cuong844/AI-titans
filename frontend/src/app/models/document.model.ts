export interface Document {
  id: number;
  title: string;
  summary: string;
  fileName: string;
  originalFileName: string;
  fileType: string;
  fileSize: number;
  filePath: string;
  sharingLevel: 'PRIVATE' | 'GROUP' | 'PUBLIC';
  department: string;
  createdBy: number;
  createdByName: string;
  createdAt: string;
  updatedAt: string;
  tags: string[];
  viewCount: number;
  starCount: number;
  content: string;
}

export interface DocumentCreateRequest {
  title: string;
  summary: string;
  fileName: string;
  originalFileName: string;
  fileType: string;
  fileSize: number;
  filePath: string;
  sharingLevel: 'PRIVATE' | 'GROUP' | 'PUBLIC';
  department: string;
  tags: string[];
  content: string;
}

export interface DocumentSearchRequest {
  keyword?: string;
  tag?: string;
  sharingLevel?: 'PRIVATE' | 'GROUP' | 'PUBLIC';
  department?: string;
  startDate?: string;
  endDate?: string;
  page: number;
  size: number;
  sortBy: string;
  sortDirection: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
