import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { DocumentService } from '../../services/document.service';
import { Document, DocumentSearchRequest, PageResponse } from '../../models/document.model';

@Component({
  selector: 'app-document-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatSelectModule,
    MatPaginatorModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="document-list-container">
      <div class="header">
        <h1>Danh sách tài liệu</h1>
        <button mat-raised-button color="primary" routerLink="/upload">
          <mat-icon>upload</mat-icon>
          Tải lên tài liệu mới
        </button>
      </div>

      <!-- Search Form -->
      <mat-card class="search-card">
        <mat-card-content>
          <form [formGroup]="searchForm" (ngSubmit)="onSearch()">
            <div class="search-row">
              <mat-form-field class="search-field">
                <mat-label>Tìm kiếm</mat-label>
                <input matInput formControlName="keyword" placeholder="Tên tài liệu, tag, nội dung...">
                <mat-icon matSuffix>search</mat-icon>
              </mat-form-field>
              
              <mat-form-field class="filter-field">
                <mat-label>Mức độ chia sẻ</mat-label>
                <mat-select formControlName="sharingLevel">
                  <mat-option value="">Tất cả</mat-option>
                  <mat-option value="PUBLIC">Công khai</mat-option>
                  <mat-option value="GROUP">Nhóm</mat-option>
                  <mat-option value="PRIVATE">Riêng tư</mat-option>
                </mat-select>
              </mat-form-field>
              
              <button mat-raised-button type="submit" [disabled]="loading">
                <mat-icon>search</mat-icon>
                Tìm kiếm
              </button>
              
              <button mat-button type="button" (click)="clearSearch()">
                <mat-icon>clear</mat-icon>
                Xóa bộ lọc
              </button>
            </div>
          </form>
        </mat-card-content>
      </mat-card>

      <!-- Results -->
      <div class="results">
        <div *ngIf="loading" class="loading">
          <mat-spinner></mat-spinner>
          <p>Đang tải dữ liệu...</p>
        </div>

        <div *ngIf="!loading && documents.length === 0" class="no-data">
          <mat-icon>folder_open</mat-icon>
          <h3>Không tìm thấy tài liệu nào</h3>
          <p>Hãy thử thay đổi từ khóa tìm kiếm hoặc tải lên tài liệu mới</p>
        </div>

        <div *ngIf="!loading && documents.length > 0" class="document-grid">
          <mat-card *ngFor="let doc of documents" class="document-card" 
                    (click)="viewDocument(doc.id)">
            <mat-card-header>
              <mat-card-title>{{ doc.title }}</mat-card-title>
              <mat-card-subtitle>
                <span class="author">{{ doc.createdByName }}</span>
                <span class="date">{{ formatDate(doc.createdAt) }}</span>
              </mat-card-subtitle>
            </mat-card-header>
            
            <mat-card-content>
              <p class="summary">{{ doc.summary || 'Không có tóm tắt' }}</p>
              
              <div class="document-meta">
                <span class="sharing-level" [ngClass]="doc.sharingLevel.toLowerCase()">
                  {{ getSharingLevelText(doc.sharingLevel) }}
                </span>
                <span class="file-type">{{ doc.fileType }}</span>
                <span class="file-size">{{ formatFileSize(doc.fileSize) }}</span>
              </div>
              
              <div class="stats">
                <span class="views">
                  <mat-icon>visibility</mat-icon>
                  {{ doc.viewCount }}
                </span>
                <span class="stars">
                  <mat-icon>star</mat-icon>
                  {{ doc.starCount }}
                </span>
              </div>
              
              <div *ngIf="doc.tags && doc.tags.length > 0" class="tags">
                <mat-chip *ngFor="let tag of doc.tags" class="tag">{{ tag }}</mat-chip>
              </div>
            </mat-card-content>
            
            <mat-card-actions>
              <button mat-button (click)="viewDocument(doc.id); $event.stopPropagation()">
                <mat-icon>visibility</mat-icon>
                Xem chi tiết
              </button>
              <button mat-button (click)="starDocument(doc.id); $event.stopPropagation()">
                <mat-icon>star</mat-icon>
                Đánh dấu sao
              </button>
            </mat-card-actions>
          </mat-card>
        </div>

        <!-- Pagination -->
        <mat-paginator *ngIf="!loading && totalElements > 0"
                        [length]="totalElements"
                        [pageSize]="pageSize"
                        [pageIndex]="currentPage"
                        [pageSizeOptions]="[5, 10, 25, 50]"
                        (page)="onPageChange($event)"
                        showFirstLastButtons>
        </mat-paginator>
      </div>
    </div>
  `,
  styles: [`
    .document-list-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 20px;
    }
    
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
    }
    
    .search-card {
      margin-bottom: 30px;
    }
    
    .search-row {
      display: flex;
      gap: 15px;
      align-items: center;
      flex-wrap: wrap;
    }
    
    .search-field {
      flex: 1;
      min-width: 200px;
    }
    
    .filter-field {
      min-width: 150px;
    }
    
    .loading, .no-data {
      text-align: center;
      padding: 40px;
      color: #666;
    }
    
    .no-data mat-icon {
      font-size: 48px;
      width: 48px;
      height: 48px;
      margin-bottom: 16px;
    }
    
    .document-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
    }
    
    .document-card {
      cursor: pointer;
      transition: all 0.3s ease;
    }
    
    .document-card:hover {
      box-shadow: 0 8px 16px rgba(0,0,0,0.1);
      transform: translateY(-4px);
    }
    
    .summary {
      color: #666;
      line-height: 1.4;
      margin: 10px 0;
    }
    
    .document-meta {
      display: flex;
      gap: 10px;
      margin: 10px 0;
      flex-wrap: wrap;
    }
    
    .stats {
      display: flex;
      gap: 15px;
      margin: 10px 0;
      color: #888;
      font-size: 0.9rem;
    }
    
    .stats mat-icon {
      font-size: 16px;
      width: 16px;
      height: 16px;
      margin-right: 4px;
    }
    
    .tags {
      margin-top: 10px;
    }
    
    .author {
      font-weight: 500;
      color: #1976d2;
    }
    
    .date {
      color: #888;
      font-size: 0.9rem;
    }
  `]
})
export class DocumentListComponent implements OnInit {
  searchForm: FormGroup;
  documents: Document[] = [];
  totalElements = 0;
  currentPage = 0;
  pageSize = 10;
  loading = false;

  constructor(
    private formBuilder: FormBuilder,
    private documentService: DocumentService,
    private router: Router
  ) {
    this.searchForm = this.formBuilder.group({
      keyword: [''],
      sharingLevel: ['']
    });
  }

  ngOnInit() {
    this.loadDocuments();
  }

  loadDocuments() {
    this.loading = true;
    this.documentService.getDocuments(this.currentPage, this.pageSize).subscribe({
      next: (response: PageResponse<Document>) => {
        this.documents = response.content;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading documents:', error);
        this.loading = false;
      }
    });
  }

  onSearch() {
    const formValue = this.searchForm.value;
    const searchRequest: DocumentSearchRequest = {
      keyword: formValue.keyword || undefined,
      sharingLevel: formValue.sharingLevel || undefined,
      page: 0,
      size: this.pageSize,
      sortBy: 'createdAt',
      sortDirection: 'desc'
    };

    this.currentPage = 0;
    this.loading = true;
    this.documentService.searchDocuments(searchRequest).subscribe({
      next: (response: PageResponse<Document>) => {
        this.documents = response.content;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error searching documents:', error);
        this.loading = false;
      }
    });
  }

  clearSearch() {
    this.searchForm.reset();
    this.currentPage = 0;
    this.loadDocuments();
  }

  onPageChange(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadDocuments();
  }

  viewDocument(id: number) {
    this.router.navigate(['/documents', id]);
  }

  starDocument(id: number) {
    this.documentService.starDocument(id).subscribe({
      next: (response) => {
        console.log('Document starred successfully');
        // Reload the document to update star count
        this.loadDocuments();
      },
      error: (error) => {
        console.error('Error starring document:', error);
      }
    });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN');
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  getSharingLevelText(level: string): string {
    switch (level) {
      case 'PRIVATE': return 'Riêng tư';
      case 'GROUP': return 'Nhóm';
      case 'PUBLIC': return 'Công khai';
      default: return level;
    }
  }
}
