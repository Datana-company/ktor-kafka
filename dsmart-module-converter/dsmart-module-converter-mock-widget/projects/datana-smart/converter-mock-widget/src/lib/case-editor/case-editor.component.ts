import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {takeUntil} from "rxjs/operators";
import {CaseEditorService} from "./case-editor.service";
import {Subject} from "rxjs";
import {FileUploadComponent} from "../file-upload/file-upload.component";

@Component({
    selector: 'case-editor-component',
    templateUrl: './case-editor.component.html',
    styleUrls: ['./case-editor.component.css']
})
export class CaseEditorComponent implements OnInit {

    @Output() newCase = new EventEmitter<String>();

    @ViewChild(FileUploadComponent) fileUpload: FileUploadComponent;

    _unsubscribe = new Subject<void>();

    caseEditorForm = this.formBuilder.group({
        caseName: ['', Validators.required],
        meltInfo: this.formBuilder.group({
            meltNumber: ['', Validators.required],
            steelGrade: [''],
            crewNumber: [''],
            shiftNumber: [''],
            mode: ['emulation'],
            devices: this.formBuilder.group({
                irCamera: this.formBuilder.group({
                    id: [''],
                    name: [''],
                    uri: [''],
                    type: ['file'],
                    deviceType: ['ConverterDevicesIrCamerta']
                })
            })
        })
    });

    constructor(private formBuilder: FormBuilder, private service: CaseEditorService) {
    }

    loadTestData() {
        this.caseEditorForm.patchValue({
            caseName: 'case-SuperPuper-' + (new Date()).getTime(),
            meltInfo: {
                meltNumber: '12_плавка',
                steelGrade: '12Х18Н10Т',
                crewNumber: 'Бр.№7',
                shiftNumber: '2-ая смена',
                mode: 'emulation',
                devices: {
                    irCamera: {
                        id: 'Cam#55',
                        name: 'CameraName',
                        uri: 'URI',
                        type: 'file',
                        deviceType: 'ConverterDevicesIrCamerta'
                    }
                }
            }
        });
    }

    onSubmit() {
        console.log(this.caseEditorForm.value);
        this.service.addCase(this.caseEditorForm.value).pipe(
            takeUntil(this._unsubscribe)
        ).subscribe(data => {
            console.log(data);
            this.newCase.emit("newCase");
            // this.fileUpload.upload(data, this.caseEditorForm.value.meltInfo.devices.irCamera.id);
            this.fileUpload.setNewCaseFolderName(data);
            this.fileUpload.setFileName(this.caseEditorForm.value.meltInfo.devices.irCamera.id);
            this.fileUpload.onFormSubmit();
        });
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
        this._unsubscribe.next();
        this._unsubscribe.complete();
    }
}
