import {Component, OnInit} from '@angular/core';
import {HttpEventType, HttpResponse} from "@angular/common/http";
import {FileUploadService} from "./file-upload.service";

@Component({
    selector: 'file-upload-component',
    templateUrl: './file-upload.component.html',
    styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {

    fileForUpload: File;

    percentCompleted: number = 0;
    isUploaded = false;

    constructor(private fuService: FileUploadService) {
    }

    setUploadFile(files: File[]) {
        console.log('Filename: ' + files[0].name);
        this.fileForUpload = files[0];
    }

    upload(newCaseFolderName, fileName) {
        console.log('+++ Uploading single file +++');
        console.log('Filename: ' + this.fileForUpload.name);
        this.isUploaded = false;

        const formData = new FormData();
        formData.append("new_case_folder_name", newCaseFolderName)
        formData.append("file_name", fileName)
        formData.append("file", this.fileForUpload);
        this.fuService.uploadWithProgress(formData)
            .subscribe(event => {
                if (event.type === HttpEventType.UploadProgress) {
                    this.percentCompleted = Math.round(100 * event.loaded / event.total);
                    console.log(this.percentCompleted + "%");
                } else if (event instanceof HttpResponse) {
                    this.isUploaded = true;
                }
            });
    }

    ngOnInit(): void {
    }
}
