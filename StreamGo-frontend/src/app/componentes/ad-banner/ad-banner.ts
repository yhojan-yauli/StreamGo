import { Component, Input, AfterViewInit, OnDestroy, ElementRef, ViewChild, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ad-banner',
  imports: [CommonModule],
  templateUrl: './ad-banner.html',
  styleUrl: './ad-banner.scss',
})
export class AdBannerComponent implements AfterViewInit, OnDestroy {
  @Input() html: string = '';
  @ViewChild('adContainer') adContainer!: ElementRef<HTMLDivElement>;

  private scripts: HTMLScriptElement[] = [];

  constructor(private renderer: Renderer2) {}

  ngAfterViewInit(): void {
    if (!this.html || !this.adContainer) return;
    this.injectAd();
  }

  private injectAd(): void {
    const container = this.adContainer.nativeElement;
    const temp = document.createElement('div');
    temp.innerHTML = this.html;

    const nodes = Array.from(temp.childNodes);

    for (const node of nodes) {
      if (node.nodeName === 'SCRIPT') {
        const original = node as HTMLScriptElement;
        const script = this.renderer.createElement('script');

        for (const attr of Array.from(original.attributes)) {
          this.renderer.setAttribute(script, attr.name, attr.value);
        }

        if (original.src) {
          this.renderer.setAttribute(script, 'src', original.src);
        } else {
          script.textContent = original.textContent;
        }

        this.renderer.appendChild(container, script);
        this.scripts.push(script);
      } else {
        this.renderer.appendChild(container, node);
      }
    }
  }

  ngOnDestroy(): void {
    this.scripts = [];
    if (this.adContainer) {
      this.adContainer.nativeElement.innerHTML = '';
    }
  }
}
