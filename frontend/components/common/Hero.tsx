import { Sparkles, Database, BrainCircuit } from "lucide-react";

export default function Hero() {
  return (
    <section className="text-center py-16">

      <div className="inline-flex items-center gap-2 rounded-full border bg-blue-50 px-4 py-2 text-sm font-medium text-blue-700">
        <Sparkles size={16} />
        AI Powered CSV Importer
      </div>

      <h1 className="mt-8 text-6xl font-extrabold tracking-tight">
        GrowEasy AI Importer
      </h1>

      <p className="mt-6 mx-auto max-w-3xl text-xl text-gray-600">
        Import Facebook Leads, Google Ads exports, Excel sheets,
        Real Estate CRM exports, or any custom CSV.
        Our AI intelligently maps every record into the GrowEasy CRM format.
      </p>

      <div className="mt-10 flex flex-wrap justify-center gap-4">

        <div className="rounded-full border px-5 py-3 flex items-center gap-2">
          <BrainCircuit size={18} />
          AI Mapping
        </div>

        <div className="rounded-full border px-5 py-3 flex items-center gap-2">
          <Database size={18} />
          CRM Compatible
        </div>

        <div className="rounded-full border px-5 py-3 flex items-center gap-2">
          <Sparkles size={18} />
          Smart Parsing
        </div>

      </div>

    </section>
  );
}