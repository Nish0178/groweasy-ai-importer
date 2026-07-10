import Hero from "@/components/common/Hero";
import UploadSection from "@/components/upload/UploadSection";
import HowItWorks from "@/components/common/HowItWorks";
import Stats from "@/components/common/Stats";

export default function Home() {
  return (
    <main className="min-h-screen bg-gradient-to-b from-slate-50 via-white to-blue-50">
      <div className="mx-auto max-w-7xl px-6 py-16">
        <Hero />

        <UploadSection />

        <HowItWorks />

        <Stats />
      </div>
    </main>
  );
}