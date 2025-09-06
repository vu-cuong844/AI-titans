export interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  department: string;
  role: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  fullName: string;
  email: string;
  department: string;
  role: string;
}
