import {Component, OnInit} from '@angular/core';
import {HttpEventType, HttpResponse} from "@angular/common/http";
import {FileUploadService} from "./file-upload.service";
import {FormArray, FormControl, FormBuilder, Validators} from "@angular/forms";
import {fileExtensionValidator} from "./file-extension-validator.directive";

@Component({
    selector: 'file-upload-component',
    templateUrl: './file-upload.component.html',
    styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {

    fileForUpload: File;

    percentCompleted: number = 0;
    isMultipleUploaded = false;
    isSingleUploaded = false;
    urlAfterUpload = '';
    percentUploaded = [0];
    acceptedExtensions = "mp4, avi, mov, ravi";
    isUploaded = false;

    constructor(private formBuilder: FormBuilder, private fuService: FileUploadService) {
    }

    // setUploadFile(files: File[]) {
    //     console.log('Filename: ' + files[0].name);
    //     this.fileForUpload = files[0];
    // }
    //
    // upload(newCaseFolderName, fileName) {
    //     console.log('+++ Uploading single file +++');
    //     console.log('Filename: ' + this.fileForUpload.name);
    //     this.isUploaded = false;
    //
    //     const formData = new FormData();
    //     formData.append("newCaseFolderName", newCaseFolderName)
    //     formData.append("fileName", fileName)
    //     formData.append("file", this.fileForUpload);
    //     this.fuService.uploadWithProgress(formData)
    //         .subscribe(event => {
    //             if (event.type === HttpEventType.UploadProgress) {
    //                 this.percentCompleted = Math.round(100 * event.loaded / event.total);
    //                 console.log(this.percentCompleted + "%");
    //             } else if (event instanceof HttpResponse) {
    //                 this.isUploaded = true;
    //             }
    //         });
    // }

    newCaseFolderName: string;
    fileName: string;
    setNewCaseFolderName(newCaseFolderName) {
        console.log(" --- newCaseFolderName: " + newCaseFolderName)
        this.newCaseFolderName = newCaseFolderName;
    }
    setFileName(fileName) {
        console.log(" --- fileName: " + fileName)
        this.fileName = fileName;
    }
    uploadForm = this.formBuilder.group({
        filesToUpload: this.formBuilder.array([
            this.formBuilder.control('', [Validators.required, fileExtensionValidator(this.acceptedExtensions)])
        ])
    });
    get filesToUpload(): FormArray {
        return this.uploadForm.get('filesToUpload') as FormArray;
    }
    addMoreFiles() {
        this.filesToUpload.push(this.formBuilder.control('',
            [Validators.required, fileExtensionValidator(this.acceptedExtensions)]));
        this.percentUploaded.push(0);
    }
    deleteFile(index: number) {
        this.filesToUpload.removeAt(index);
        this.percentUploaded.splice(index, 1);
    }
    onFormSubmit() {
        console.log('---Uploading multiple file---');
        console.log(" --- newCaseFolderName: " + this.newCaseFolderName)
        this.isMultipleUploaded = false;
        for (let i = 0; i < this.filesToUpload.length && this.uploadForm.valid; i++) {
            const selectedFileList = (<HTMLInputElement>document.getElementById('file' + i)).files;
            const file = selectedFileList.item(0);
            this.uploadFile(file, i);
        }
    }
    uploadFile(file: File, fileNum: number) {
        const formData = new FormData();
        formData.append("newCaseFolderName", this.newCaseFolderName);
        formData.append("fileName", this.fileName + "_" + fileNum);
        formData.append("file", file);
        this.fuService.uploadWithProgress(formData)
            .subscribe(event => {
                    if (event.type === HttpEventType.UploadProgress) {
                        this.percentUploaded[fileNum] = Math.round(100 * event.loaded / event.total);
                    } else if (event instanceof HttpResponse) {
                        console.log(file.name + ', Size: ' + file.size);
                        this.fileUploadSuccess();
                    }
                },
                err => console.log(err)
            );
    }
    fileUploadSuccess() {
        let flag = true;
        this.percentUploaded.forEach(n => {
            if (n !== 100) {
                flag = false;
            }
        });
        if (flag) {
            this.isMultipleUploaded = true;
        }
    }
    formReset() {
        this.uploadForm.reset();
        this.isMultipleUploaded = false;
        for (let i = 0; i < this.percentUploaded.length; i++) {
            this.percentUploaded[i] = 0;
        }
    }

    ngOnInit(): void {
    }
}
