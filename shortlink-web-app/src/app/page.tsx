import { Header } from "@/components/header";
import { UrlShortener } from "@/components/url-shortener";

export default function Home() {
  return (
    <div className="min-h-screen flex flex-col bg-secondary/50 dark:bg-background">
      <Header />
      <main className="flex-grow container mx-auto p-4 sm:p-6 lg:p-8">
        <UrlShortener />
      </main>
    </div>
  );
}