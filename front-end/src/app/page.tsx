import Image from "next/image";
import styles from "./page.module.css";
import Link from "next/link";

export default function Home() {
  return (
    <>
     <Link href="/auth/login">Login</Link><br></br>
     <Link href="/auth/register">Register</Link>
    </>
  );
}
