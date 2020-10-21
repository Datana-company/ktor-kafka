import {Component, OnInit} from '@angular/core';
import {HttpEventType, HttpResponse} from "@angular/common/http";
import {FileUploadService} from "../case-editor/file-upload.service";

@Component({
    selector: 'file-upload-component',
    templateUrl: './file-upload.component.html',
    styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {

    percentCompleted: number = 0;
    isUploaded = false;

    constructor(private fuService: FileUploadService) {
    }

    upload(files: File[]) {
        console.log('+++ Uploading single file +++');
        const file = files[0];
        console.log('Filename: ' + file.name);
        this.isUploaded = false;

        const formData = new FormData();
        formData.append("file", file);
        this.fuService.uploadWithProgress(formData)
            .subscribe(event => {
                if (event.type === HttpEventType.UploadProgress) {
                    this.percentCompleted = Math.round(100 * event.loaded / event.total);
                } else if (event instanceof HttpResponse) {
                    this.isUploaded = true;
                }
            });
    }

    ngOnInit(): void {
    }
}
