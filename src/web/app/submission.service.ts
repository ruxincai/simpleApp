import {Injectable} from 'angular2/core';
import {EventEmitter} from 'angular2/core';
import {Subscription} from 'rxjs/Subscription';
import {SubmissionType} from "./main";
import {httpGet} from "./http";
import {extractJSON} from "./http";

@Injectable()
export class SubmissionService {
	submissions: SubmissionType[] = [];

	constructor() {
		this.reload();
	}

	reload() {
		httpGet('api/submissions')
				.then(extractJSON)
				.then(list => this.submissions = list)
				.catch(error => {
					console.log(error);
				});
	}
}
