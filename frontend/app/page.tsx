import Hero from "@/components/common/Hero";
import UploadSection from "@/components/upload/UploadSection";

export default function Home() {
  return (
    <main className="min-h-screen">
      <div className="mx-auto max-w-6xl px-6 py-20">
        <Hero />
        <UploadSection />
      </div>
    </main>
  );
}