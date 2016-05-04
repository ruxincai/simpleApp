import {ComponentRef, ChangeDetectorRef} from 'angular2/core';
import {DialogLoader} from "./dialog-loader.service";

export abstract class Dialog {
	ref: ComponentRef;
	loader: DialogLoader;

	init(ref: ComponentRef, loader: DialogLoader, data: any) {
		this.ref = ref;
		this.loader = loader;
		this.setData(data);

		let box = ref.location.nativeElement;
		let form : HTMLFormElement = box.firstElementChild;
		form.style.left = (box.offsetWidth - form.offsetWidth) / 2 + 'px';
		form.style.top = (box.offsetHeight - form.offsetHeight) / 2 + 'px';
		let elements = form.elements;
		if (elements) {
			(<HTMLInputElement> elements[0]).focus();
		}
	}

	close() {
		this.ref.dispose();
	}

	abstract setData(data: any): void;
}
