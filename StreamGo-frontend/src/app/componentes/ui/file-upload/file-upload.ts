import { Component, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './file-upload.html',
  styleUrl: './file-upload.scss',
})
export class FileUploadComponent {
  @Input() label = 'Subir archivo';
  @Input() accept = 'image/*';
  @Input() uploading = false;
  @Input() progress = 0;
  @Input() previewUrl: string | null = null;
  @Input() disabled = false;
  @Input() compact = false;
  @Output() filesChange = new EventEmitter<FileList>();

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  dragOver = false;
  selectedFile: File | null = null;

  onDragOver(event: DragEvent): void {
    if (this.disabled) return;
    event.preventDefault();
    this.dragOver = true;
  }

  onDragLeave(event: DragEvent): void {
    if (this.disabled) return;
    event.preventDefault();
    this.dragOver = false;
  }

  onDrop(event: DragEvent): void {
    if (this.disabled) return;
    event.preventDefault();
    this.dragOver = false;
    const files = event.dataTransfer?.files;
    if (files && files.length) {
      this.selectedFile = files[0];
      this.filesChange.emit(files);
    }
  }

  onFileSelect(event: Event): void {
    if (this.disabled) return;
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.selectedFile = input.files[0];
      this.filesChange.emit(input.files);
    }
  }

  triggerClick(): void {
    if (this.disabled) return;
    this.fileInput.nativeElement.click();
  }

  get fileName(): string {
    return this.selectedFile?.name || '';
  }
}
