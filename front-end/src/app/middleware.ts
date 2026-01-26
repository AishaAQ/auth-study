import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'
 
export async function middleware(request: NextRequest) {

    const cookie = request.cookies.get("SessionToken");
    
    if (!cookie) return NextResponse.redirect(new URL("/login", request.url));

    const res = await fetch("https://localhost:8080/sessions/validate", {
        headers: {
            Cookie: `SessionToken=${cookie.value}`,
        },
        credentials: "include",
    });

  if (!res.ok) {
    return NextResponse.redirect(new URL("/login", request.url));
  }

  return NextResponse.next();

}
 
// See "Matching Paths" below to learn more
export const config = {
  matcher: '/about/:path*',
}