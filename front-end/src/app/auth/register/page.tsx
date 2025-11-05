import Link from 'next/link'
import styles from '@/app/auth/auth.module.css'
import PasswordField from '@/components/PasswordField'

function page() {

  return (
    <>
        <div className={styles.authPage}>
            <div className={styles.authCard}>
                <h1>Create your account</h1>
                <form>
                    <div>
                        <label htmlFor="email">Email address</label>
                        <input type="email" id="email" name="email" required></input>
                    </div>
                    <PasswordField />
                    <button type="submit" className={styles.customButton}>Register</button>
                </form>
                <p>OR</p>
                <div>
                    <button>Continue with Google</button>
                </div>

                <p>Already have an account? <Link href="/auth/login">Log in</Link></p>
            
            </div>
   
        </div>
    </>
  )
}

export default page