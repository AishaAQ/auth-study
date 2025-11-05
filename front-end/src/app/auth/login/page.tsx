import React from 'react'
import Link from 'next/link'

function page() {
  return (
        <>
        <h1>Log in</h1>
        <div>
            <form>
                <div>
                    <label htmlFor="username">Email</label>
                    <input type="text" id="username" name="username" required></input>
                </div>
                <div>
                    <label htmlFor="password">Password</label>
                    <input type="password" id="password" name="password" required></input>
                    <Link href="/">Forgot password?</Link>
                </div>
                <button type="submit">Register</button>
            </form>
            <p>OR</p>
            <div>
                <button>Continue with Google</button>
            </div>

            <p>Don't have an account? <Link href="/auth/register">Register</Link></p>
            
        </div>
    </>
  )
}

export default page