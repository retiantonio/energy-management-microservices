import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { LoginPage } from './login';

describe('LoginPage', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginPage],
      providers: [provideHttpClient(), provideRouter([])],
    }).compileComponents();
  });

  it('should render the login form', async () => {
    const fixture = TestBed.createComponent(LoginPage);
    await fixture.whenStable();
    const page = fixture.nativeElement as HTMLElement;

    expect(page.querySelector('h1')?.textContent).toContain('Welcome back');
    expect(page.querySelector('input[name="username"]')).not.toBeNull();
    expect(page.querySelector('input[name="password"]')).not.toBeNull();
  });
});
