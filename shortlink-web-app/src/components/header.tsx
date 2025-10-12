import { Link as LinkIcon } from "lucide-react";
import { ThemeToggle } from "./theme-toggle";

export function Header() {
  return (
    <header className="py-4 px-6 border-b">
      <div className="container mx-auto flex items-center justify-between">
        <div className="flex items-center gap-2">
          <LinkIcon className="h-6 w-6" />
          <h1 className="text-2xl font-bold">Encurtai</h1>
        </div>
        <ThemeToggle />
      </div>
    </header>
  );
}