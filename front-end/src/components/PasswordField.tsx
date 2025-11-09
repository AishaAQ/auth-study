'use client'
import { useState, useRef } from 'react'
import styles from '@/components/PasswordField.module.css'
import { FaEye, FaEyeSlash } from 'react-icons/fa'

function PasswordField() {

    const [showPassword, setShowPassword] = useState(false);

    return (
        <div className={styles.passwordContainer}>
            <label htmlFor="password">Password</label>
            <input 
                type={showPassword ? "text" : "password"} 
                id="password" name="password" required></input>
            <button 
                type="button" 
                className={styles.togglePassword}
                onClick={() => {setShowPassword(!showPassword);} } 
                aria-label={showPassword ? "Hide password" : "Show password"}
            >
                {showPassword ? <FaEyeSlash/> : <FaEye/>}
            </button>
        </div>
  )
}

export default PasswordField