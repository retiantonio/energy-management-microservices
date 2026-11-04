import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RegisterPage } from './register';

describe('RegisterPage', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterPage],
      providers: [provideHttpClient(), provideRouter([])],
    }).compileComponents();
  });

  it('should render all required registration fields', async () => {
    const fixture = TestBed.createComponent(RegisterPage);
    await fixture.whenStable();
    const page = fixture.nativeElement as HTMLElement;

    expect(page.querySelector('input[name="username"]')).not.toBeNull();
    expect(page.querySelector('input[name="password"]')).not.toBeNull();
    expect(page.querySelector('input[name="name"]')).not.toBeNull();
    expect(page.querySelector('input[name="email"]')).not.toBeNull();
    expect(page.querySelector('input[name="address"]')).not.toBeNull();
  });
});
