import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DocumentService } from '../../services/document.service';
import { DocumentCreateRequest } from '../../models/document.model';

@Component({
  selector: 'app-document-upload',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="upload-container">
      <div class="header">
        <h1>Tải lên tài liệu mới</h1>
        <button mat-button (click)="goBack()">
          <mat-icon>arrow_back</mat-icon>
          Quay lại
        </button>
      </div>

      <mat-card class="upload-card">
        <mat-card-content>
          <form [formGroup]="uploadForm" (ngSubmit)="onSubmit()">
            <!-- File Upload -->
            <div class="file-upload-section">
              <h3>Chọn file</h3>
              <div class="file-upload-area" 
                   (click)="fileInput.click()"
                   [class.dragover]="isDragOver"
                   (dragover)="onDragOver($event)"
                   (dragleave)="onDragLeave($event)"
                   (drop)="onDrop($event)">
                <input #fileInput type="file" 
                       (change)="onFileSelected($event)"
                       accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
                       multiple
                       style="display: none;">
                
                <div *ngIf="selectedFiles.length === 0" class="upload-placeholder">
                  <mat-icon>cloud_upload</mat-icon>
                  <p>Kéo thả file vào đây hoặc click để chọn file</p>
                  <p class="file-types">Hỗ trợ: PDF, DOC, DOCX, JPG, PNG (tối đa 10MB mỗi file, tối đa 5 file)</p>
                </div>
                
                <div *ngIf="selectedFiles.length > 0" class="files-selected">
                  <div *ngFor="let file of selectedFiles; let i = index" class="file-selected">
                    <mat-icon>description</mat-icon>
                    <div class="file-info">
                      <p class="file-name">{{ file.name }}</p>
                      <p class="file-size">{{ formatFileSize(file.size) }}</p>
                    </div>
                    <button mat-icon-button (click)="removeFile(i)" type="button">
                      <mat-icon>close</mat-icon>
                    </button>
                  </div>
                </div>
              </div>
              
              <div *ngIf="uploadForm.get('files')?.hasError('required') && uploadForm.get('files')?.touched" 
                   class="error-message">
                Vui lòng chọn ít nhất một file
              </div>
              <div *ngIf="selectedFiles.length > 5" class="error-message">
                Chỉ được chọn tối đa 5 file
              </div>
            </div>

            <!-- Document Details -->
            <div class="form-section">
              <h3>Thông tin tài liệu</h3>
              
              <mat-form-field class="full-width">
                <mat-label>Tiêu đề *</mat-label>
                <input matInput formControlName="title" placeholder="Nhập tiêu đề tài liệu">
                <mat-error *ngIf="uploadForm.get('title')?.hasError('required')">
                  Tiêu đề là bắt buộc
                </mat-error>
                <mat-error *ngIf="uploadForm.get('title')?.hasError('maxlength')">
                  Tiêu đề không được vượt quá 200 ký tự
                </mat-error>
              </mat-form-field>

              <mat-form-field class="full-width">
                <mat-label>Tóm tắt</mat-label>
                <textarea matInput formControlName="summary" 
                          placeholder="Nhập tóm tắt nội dung tài liệu (tối đa 500 từ) hoặc để trống để tự động tạo"
                          rows="4"></textarea>
                <mat-hint>{{ getSummaryWordCount() }}/500 từ</mat-hint>
                <mat-error *ngIf="uploadForm.get('summary')?.hasError('maxlength')">
                  Tóm tắt không được vượt quá 500 từ
                </mat-error>
                <button mat-icon-button matSuffix (click)="generateAISummary()" 
                        [disabled]="!selectedFiles.length" 
                        matTooltip="Tự động tạo tóm tắt bằng AI">
                  <mat-icon>auto_awesome</mat-icon>
                </button>
              </mat-form-field>

              <mat-form-field class="half-width">
                <mat-label>Mức độ chia sẻ *</mat-label>
                <mat-select formControlName="sharingLevel">
                  <mat-option value="PRIVATE">Riêng tư - Chỉ tôi có thể xem</mat-option>
                  <mat-option value="GROUP">Nhóm - Chỉ phòng ban có thể xem</mat-option>
                  <mat-option value="PUBLIC">Công khai - Tất cả nhân viên có thể xem</mat-option>
                </mat-select>
                <mat-error *ngIf="uploadForm.get('sharingLevel')?.hasError('required')">
                  Vui lòng chọn mức độ chia sẻ
                </mat-error>
              </mat-form-field>

              <mat-form-field class="half-width">
                <mat-label>Tags (tùy chọn)</mat-label>
                <mat-chip-grid #chipGrid aria-label="Tags">
                  <mat-chip *ngFor="let tag of tags" (removed)="removeTag(tag)">
                    {{ tag }}
                    <button matChipRemove>
                      <mat-icon>cancel</mat-icon>
                    </button>
                  </mat-chip>
                </mat-chip-grid>
                <input placeholder="Nhập tag và nhấn Enter"
                       [matChipInputFor]="chipGrid"
                       [matChipInputSeparatorKeyCodes]="separatorKeysCodes"
                       (matChipInputTokenEnd)="addTag($event)">
                <button mat-icon-button matSuffix (click)="generateAITags()" 
                        [disabled]="!selectedFiles.length" 
                        matTooltip="Tự động tạo tags bằng AI">
                  <mat-icon>auto_awesome</mat-icon>
                </button>
              </mat-form-field>
            </div>

            <!-- Actions -->
            <div class="form-actions">
              <button mat-button type="button" (click)="goBack()">
                Hủy
              </button>
              <button mat-raised-button color="primary" type="submit" 
                      [disabled]="uploadForm.invalid || uploading">
                <mat-spinner *ngIf="uploading" diameter="20"></mat-spinner>
                <span *ngIf="!uploading">Tải lên tài liệu</span>
              </button>
            </div>
          </form>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .upload-container {
      max-width: 800px;
      margin: 0 auto;
      padding: 20px;
    }
    
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
    }
    
    .upload-card {
      margin-bottom: 20px;
    }
    
    .file-upload-section, .form-section {
      margin-bottom: 30px;
    }
    
    .file-upload-area {
      border: 2px dashed #ccc;
      border-radius: 8px;
      padding: 40px;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s ease;
      margin-bottom: 10px;
    }
    
    .file-upload-area:hover,
    .file-upload-area.dragover {
      border-color: #1976d2;
      background-color: #f5f5f5;
    }
    
    .upload-placeholder mat-icon {
      font-size: 48px;
      width: 48px;
      height: 48px;
      color: #ccc;
      margin-bottom: 16px;
    }
    
    .files-selected {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }
    
    .file-selected {
      display: flex;
      align-items: center;
      gap: 15px;
      padding: 10px;
      border: 1px solid #e0e0e0;
      border-radius: 4px;
      background-color: #f9f9f9;
    }
    
    .file-selected mat-icon {
      font-size: 32px;
      width: 32px;
      height: 32px;
      color: #1976d2;
    }
    
    .file-info {
      flex: 1;
      text-align: left;
    }
    
    .file-name {
      font-weight: 500;
      margin: 0 0 5px 0;
    }
    
    .file-size {
      color: #666;
      font-size: 0.9rem;
      margin: 0;
    }
    
    .file-types {
      color: #666;
      font-size: 0.9rem;
      margin: 10px 0 0 0;
    }
    
    .half-width {
      width: calc(50% - 10px);
      margin-right: 20px;
    }
    
    .half-width:last-child {
      margin-right: 0;
    }
    
    .form-actions {
      display: flex;
      justify-content: flex-end;
      gap: 15px;
      margin-top: 30px;
    }
    
    .error-message {
      color: #f44336;
      font-size: 0.75rem;
      margin-top: 5px;
    }
    
    mat-spinner {
      margin-right: 10px;
    }
  `]
})
export class DocumentUploadComponent implements OnInit {
  uploadForm: FormGroup;
  selectedFiles: File[] = [];
  tags: string[] = [];
  uploading = false;
  isDragOver = false;
  
  readonly separatorKeysCodes = [13, 188] as const; // Enter and comma

  constructor(
    private formBuilder: FormBuilder,
    private documentService: DocumentService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.uploadForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.maxLength(200)]],
      summary: ['', [Validators.maxLength(500)]],
      sharingLevel: ['', Validators.required],
      files: [null, Validators.required]
    });
  }

  ngOnInit() {
    // Set default sharing level
    this.uploadForm.patchValue({
      sharingLevel: 'GROUP'
    });
  }

  onFileSelected(event: any) {
    const files = Array.from(event.target.files);
    if (files.length > 0) {
      this.validateAndSetFiles(files);
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragOver = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragOver = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragOver = false;
    
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.validateAndSetFiles(Array.from(files));
    }
  }

  validateAndSetFiles(files: File[]) {
    const validFiles: File[] = [];
    const allowedTypes = [
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'image/jpeg',
      'image/jpg',
      'image/png'
    ];

    for (const file of files) {
      // Check file size (10MB limit)
      if (file.size > 10 * 1024 * 1024) {
        this.snackBar.open(`File "${file.name}" quá lớn. Kích thước tối đa là 10MB`, 'Đóng', {
          duration: 3000
        });
        continue;
      }

      // Check file type
      if (!allowedTypes.includes(file.type)) {
        this.snackBar.open(`File "${file.name}" không được hỗ trợ. Chỉ chấp nhận PDF, DOC, DOCX, JPG, PNG`, 'Đóng', {
          duration: 3000
        });
        continue;
      }

      validFiles.push(file);
    }

    // Check total file count (max 5)
    if (this.selectedFiles.length + validFiles.length > 5) {
      this.snackBar.open('Chỉ được chọn tối đa 5 file', 'Đóng', {
        duration: 3000
      });
      return;
    }

    this.selectedFiles = [...this.selectedFiles, ...validFiles];
    this.uploadForm.patchValue({ files: this.selectedFiles });
  }

  removeFile(index: number) {
    this.selectedFiles.splice(index, 1);
    this.uploadForm.patchValue({ files: this.selectedFiles });
  }

  addTag(event: any) {
    const value = (event.value || '').trim();
    if (value && !this.tags.includes(value)) {
      this.tags.push(value);
    }
    event.chipInput!.clear();
  }

  removeTag(tag: string) {
    const index = this.tags.indexOf(tag);
    if (index >= 0) {
      this.tags.splice(index, 1);
    }
  }

  getSummaryWordCount(): number {
    const summary = this.uploadForm.get('summary')?.value || '';
    return summary.split(/\s+/).filter((word: string) => word.length > 0).length;
  }

  generateAISummary() {
    if (this.selectedFiles.length === 0) {
      this.snackBar.open('Vui lòng chọn file trước khi tạo tóm tắt', 'Đóng', {
        duration: 3000
      });
      return;
    }

    // Mock content for AI processing
    const mockContent = 'Nội dung tài liệu mẫu để tạo tóm tắt tự động. Trong thực tế, nội dung sẽ được trích xuất từ file đã chọn.';
    
    this.documentService.generateAISummary(mockContent).subscribe({
      next: (summary) => {
        this.uploadForm.patchValue({ summary: summary });
        this.snackBar.open('Tóm tắt đã được tạo tự động', 'Đóng', {
          duration: 3000
        });
      },
      error: (error) => {
        console.error('Error generating AI summary:', error);
        this.snackBar.open('Lỗi khi tạo tóm tắt tự động', 'Đóng', {
          duration: 3000
        });
      }
    });
  }

  generateAITags() {
    if (this.selectedFiles.length === 0) {
      this.snackBar.open('Vui lòng chọn file trước khi tạo tags', 'Đóng', {
        duration: 3000
      });
      return;
    }

    const title = this.uploadForm.get('title')?.value || '';
    const mockContent = 'Nội dung tài liệu mẫu để tạo tags tự động. Trong thực tế, nội dung sẽ được trích xuất từ file đã chọn.';
    
    this.documentService.generateAITags(mockContent, title).subscribe({
      next: (generatedTags) => {
        // Merge with existing tags, avoiding duplicates
        const existingTags = new Set(this.tags);
        const newTags = generatedTags.filter(tag => !existingTags.has(tag));
        this.tags = [...this.tags, ...newTags];
        this.snackBar.open(`${newTags.length} tags đã được tạo tự động`, 'Đóng', {
          duration: 3000
        });
      },
      error: (error) => {
        console.error('Error generating AI tags:', error);
        this.snackBar.open('Lỗi khi tạo tags tự động', 'Đóng', {
          duration: 3000
        });
      }
    });
  }

  onSubmit() {
    if (this.uploadForm.valid && this.selectedFiles.length > 0) {
      this.uploading = true;
      
      const formValue = this.uploadForm.value;
      
      // Upload multiple documents
      const uploadPromises = this.selectedFiles.map(file => {
        const createRequest: DocumentCreateRequest = {
          title: formValue.title,
          summary: formValue.summary,
          fileName: file.name,
          originalFileName: file.name,
          fileType: file.type,
          fileSize: file.size,
          filePath: '/uploads/' + file.name, // Mock path
          sharingLevel: formValue.sharingLevel,
          department: 'IT', // Mock department
          tags: this.tags,
          content: 'Mock content - in real implementation, extract content from file'
        };

        return this.documentService.createDocument(createRequest).toPromise();
      });

      Promise.all(uploadPromises).then((documents) => {
        this.uploading = false;
        this.snackBar.open(`${documents.length} tài liệu đã được tải lên thành công`, 'Đóng', {
          duration: 3000
        });
        this.router.navigate(['/home']);
      }).catch((error) => {
        this.uploading = false;
        console.error('Error uploading documents:', error);
        this.snackBar.open('Lỗi khi tải lên tài liệu', 'Đóng', {
          duration: 3000
        });
      });
    }
  }

  goBack() {
    this.router.navigate(['/home']);
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }
}

