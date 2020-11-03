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

    selectedCaseName: String ="";

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
                    deviceType: ['ConverterDevicesIrCamera']
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
                        deviceType: 'ConverterDevicesIrCamera'
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

    caseSelected(selectedCaseName) {
        console.log(" --- CaseEditorComponent::caseSelected() --- selectedCaseName: " + selectedCaseName );
        // this.selectedCaseName = " (" + selectedCaseName + ")";
        this.selectedCaseName = selectedCaseName;
        this.service.getSelectedCaseData(selectedCaseName).pipe(
            takeUntil(this._unsubscribe)
        ).subscribe(caseData => {
            console.log(caseData);
            if (caseData == null) {
                this.caseEditorForm.reset();
            } else {
                this.loadCaseToForm(caseData);
            }
        });
    }

    loadCaseToForm(caseData) {
        this.caseEditorForm.patchValue({
            caseName: this.selectedCaseName,
            meltInfo: {
                meltNumber: caseData.meltNumber,
                steelGrade: caseData.steelGrade,
                crewNumber: caseData.crewNumber,
                shiftNumber: caseData.shiftNumber,
                mode: caseData.mode,
                devices: {
                    irCamera: {
                        id: caseData.devices.irCamera.id,
                        name: caseData.devices.irCamera.name,
                        uri: caseData.devices.irCamera.uri,
                        type: caseData.devices.irCamera.type,
                        deviceType: caseData.devices.irCamera.deviceType
                    }
                }
            }
        });
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
        this._unsubscribe.next();
        this._unsubscribe.complete();
    }
}
