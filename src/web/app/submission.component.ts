import {Component, Input} from 'angular2/core';
import {SubmissionType} from "./main";
import {DialogLoader} from "./dialog-loader.service";
import {NewResponse} from "./post-response.component";
import {SubmissionService} from "./submission.service";

@Component({
	selector: 'test-submission',
	template: `
   	<div class="row">
   	<span class="link clickable" (click)="showNewResponseDialog()">Respond</span>
	<span class="col2">{{submission.userName}}</span>
	<span class="col3">{{submission.city}}</span>
	<span class="col4">{{lastUpdate}}</span>
	<span class="stretch">{{submission.text}}</span>
	</div>
	<div *ngFor="#r of submission.responses;">{{r}}</div>`
})

export class SubmissionComponent {
	private static DATE_FORMAT = {
		year: 'numeric',
		month: 'short',
		day: 'numeric',
		hour: 'numeric',
		minute: 'numeric',
		second: 'numeric'
	};
	@Input() submission: SubmissionType;
	private lastUpdate: string;

	constructor(private dialogLoader: DialogLoader,
			private submissionService: SubmissionService) {
	}

	ngOnChanges(changes: any) {
		this.lastUpdate = new Date(this.submission.last_update).
			toLocaleDateString(undefined, SubmissionComponent.DATE_FORMAT);
	}

	showNewResponseDialog() {
		this.dialogLoader.show(NewResponse, {
			service: this.submissionService,
			submission: this.submission
		});
	}
}