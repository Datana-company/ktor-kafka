import {Component, EventEmitter, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {HostService} from "../services/host.service";

@Component({
  selector: 'case-editor-component',
  templateUrl: './case-editor.component.html',
  styleUrls: ['./case-editor.component.css']
})
export class CaseEditorComponent implements OnInit, OnDestroy {

  @Output() newCase = new EventEmitter<string>();

  selectedCaseId = "";

  private unsubscribe = new Subject<void>();

  caseEditorForm;

  constructor(
    private formBuilder: FormBuilder,
    private service: HostService,
  ) {
  }

  ngOnInit(): void {
    this.caseEditorForm = this.formBuilder.group({
      caseId: ['case123', Validators.required],
      meltNumber: ['123456', Validators.required],
      steelGrade: ['XDF-234', Validators.required],
      crewNumber: ['2', Validators.required],
      shiftNumber: ['1', Validators.required],

      converterId: ['converter1', Validators.required],
      converterName: ['Конвертер им. Иванова'],

      irCameraId: ['cam1', Validators.required],
      irCameraName: [''],
      fileVideo: [''],

      selsynId: ['selsyn1', Validators.required],
      selsynName: [''],
      selsynJson: [''],

      slagRateId: ['slagrate1', Validators.required],
      slagRateName: [''],
      slagRateJson: [''],
    });
  }

  // loadTestData(): void {
  //     this.caseEditorForm.patchValue({
  //         caseName: 'case-SuperPuper-' + (new Date()).getTime(),
  //         meltInfo: {
  //             meltNumber: '12_плавка',
  //             steelGrade: '12Х18Н10Т',
  //             crewNumber: 'Бр.№7',
  //             shiftNumber: '2-ая смена',
  //             mode: 'emulation',
  //             devices: {
  //                 irCamera: {
  //                     id: 'Cam#55',
  //                     name: 'CameraName',
  //                     uri: 'URI',
  //                     type: 'file',
  //                     deviceType: 'ConverterDevicesIrCamera'
  //                 }
  //             }
  //         }
  //     });
  // }

  onSubmit(): void {
    console.log(this.caseEditorForm.value);
    this.service.addCase(this.caseEditorForm.value).pipe(
      takeUntil(this.unsubscribe)
    ).subscribe(data => {
      console.log(data);
      this.newCase.emit("newCase");

      const formData = new FormData();
      Object.assign(formData, this.caseEditorForm);
      // console.log("Sending FormData", formData);
      formData.append('fileVideo', this.caseEditorForm.get('fileVideo').value);
      formData.append('selsynJson', this.caseEditorForm.get('selsynJson').value);
      formData.append('slagRateJson', this.caseEditorForm.get('slagRateJson').value);

      this.service.addCase(formData).subscribe(
        (res) => console.log('Response form addCase in onSubmit', res),
        (err) => console.log('Error response form addCase in onSubmit', err)
      );

      // this.fileUpload.upload(data, this.caseEditorForm.value.meltInfo.devices.irCamera.id);
      // this.fileUpload.setNewCaseFolderName(data);
      // this.fileUpload.setFileName(this.caseEditorForm.value.meltInfo.devices.irCamera.id);
      // this.fileUpload.onFormSubmit();
    });
  }

  caseSelected(selectedCaseId): void {
    console.log(" --- CaseEditorComponent::caseSelected() --- selectedCaseId: " + selectedCaseId);
    // this.selectedCaseName = " (" + selectedCaseName + ")";
    this.selectedCaseId = selectedCaseId;
    this.service.getSelectedCaseData(selectedCaseId).pipe(
      takeUntil(this.unsubscribe)
    ).subscribe(caseData => {
      console.log(caseData);
      if (caseData == null) {
        this.caseEditorForm.reset();
      } else {
        this.loadCaseToForm(caseData);
      }
    });
  }

  loadCaseToForm(caseData): void {
    this.caseEditorForm.patchValue({
      caseId: this.selectedCaseId,
      meltNumber: caseData.meltNumber,
      steelGrade: caseData.steelGrade,
      crewNumber: caseData.crewNumber,
      shiftNumber: caseData.shiftNumber,

      converterId: caseData.devices.converter.id,
      converterName: caseData.devices.converter.name,

      irCameraId: caseData.devices.irCamera.id,
      irCameraName: caseData.devices.irCamera.name,
      fileVideo: null,

      selsynId: caseData.devices.selsyn.id,
      selsynName: caseData.devices.selsyn.name,
      selsynJson: null,

      slagRateId: caseData.devices.slagRate.id,
      slagRateName: caseData.devices.slagRate.name,
      slagRateJson: null,
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  onVideoFileSelect(event): void {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.caseEditorForm.get('fileVideo').setValue(file);
    }
  }

  onSelsynFileSelect(event): void {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.caseEditorForm.get('selsynJson').setValue(file);
    }
  }

  onSlagRateFileSelect(event): void {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.caseEditorForm.get('slagRateJson').setValue(file);
    }
  }
}
