import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Document, DocumentCreateRequest, DocumentSearchRequest, PageResponse } from '../models/document.model';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private readonly API_URL = 'http://localhost:8082/api/documents';

  constructor(private http: HttpClient) {}

  createDocument(document: DocumentCreateRequest): Observable<Document> {
    return this.http.post<Document>(this.API_URL, document);
  }

  getDocument(id: number): Observable<Document> {
    return this.http.get<Document>(`${this.API_URL}/${id}`);
  }

  getDocuments(page: number = 0, size: number = 10): Observable<PageResponse<Document>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Document>>(this.API_URL, { params });
  }

  getTopViewedDocuments(limit: number = 5): Observable<Document[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<Document[]>(`${this.API_URL}/top/viewed`, { params });
  }

  getTopStarredDocuments(limit: number = 5): Observable<Document[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<Document[]>(`${this.API_URL}/top/starred`, { params });
  }

  getRecentDocuments(limit: number = 5): Observable<Document[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<Document[]>(`${this.API_URL}/recent`, { params });
  }

  getMyDocuments(limit: number = 5): Observable<Document[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<Document[]>(`${this.API_URL}/my-documents`, { params });
  }

  searchDocuments(searchRequest: DocumentSearchRequest): Observable<PageResponse<Document>> {
    return this.http.post<PageResponse<Document>>(`${this.API_URL}/search`, searchRequest);
  }

  starDocument(id: number): Observable<string> {
    return this.http.post<string>(`${this.API_URL}/${id}/star`, {});
  }

  deleteDocument(id: number): Observable<string> {
    return this.http.delete<string>(`${this.API_URL}/${id}`);
  }

  generateAISummary(content: string): Observable<string> {
    return this.http.post<string>(`${this.API_URL}/ai/generate-summary`, content);
  }

  generateAITags(content: string, title: string): Observable<string[]> {
    return this.http.post<string[]>(`${this.API_URL}/ai/generate-tags?content=${encodeURIComponent(content)}&title=${encodeURIComponent(title)}`, {});
  }
}
