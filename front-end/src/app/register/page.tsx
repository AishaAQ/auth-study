import React from 'react'
import Link from 'next/link'

function page() {
  return (
    <>
        <h1>Create your account</h1>
        <div>
            <form>
                <div>
                    <label htmlFor="username">Email</label>
                    <input type="text" id="username" name="username" required></input>
                </div>
                <div>
                    <label htmlFor="password">Password</label>
                    <input type="password" id="password" name="password" required></input>
                </div>
                <button type="submit">Register</button>
            </form>
            <line>OR</line>
            <div>
                <button>Continue with Google</button>
            </div>

            <p>Already have an account? <Link href="/login">Log in</Link></p>
            
        </div>
    </>
  )
}

export default page