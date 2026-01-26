import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'
 
export async function middleware(request: NextRequest) {

    const cookie = request.cookies.get("SessionToken");
    
    if (!cookie) return NextResponse.redirect(new URL("/auth/login", request.url));

    const res = await fetch("http://localhost:8080/sessions/validation", {
        headers: {
            Cookie: `SessionToken=${cookie.value}`,
        },
        credentials: "include",
    });

  if (!res.ok) {
    return NextResponse.redirect(new URL("/auth/login", request.url));
  }

  return NextResponse.next();

}
 
export const config = {
  matcher: '/account/:path*',
}