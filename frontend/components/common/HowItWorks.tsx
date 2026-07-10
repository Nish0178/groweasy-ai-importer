import { Upload, BrainCircuit, DatabaseZap } from "lucide-react";

const steps = [
  {
    icon: Upload,
    title: "Upload CSV",
    description:
      "Upload any CSV file including Facebook Leads, Google Ads, Excel sheets, CRM exports, or custom datasets.",
  },
  {
    icon: BrainCircuit,
    title: "AI Maps Fields",
    description:
      "Our AI understands different column names and intelligently maps them into the GrowEasy CRM schema.",
  },
  {
    icon: DatabaseZap,
    title: "Import to CRM",
    description:
      "Review the extracted records and import only valid CRM-ready leads.",
  },
];

export default function HowItWorks() {
  return (
    <section className="mt-28">
      <div className="text-center">
        <h2 className="text-4xl font-bold">How It Works</h2>

        <p className="mt-3 text-slate-600">
          Import your CRM leads in just three simple steps.
        </p>
      </div>

      <div className="mt-14 grid gap-8 md:grid-cols-3">
        {steps.map((step, index) => {
          const Icon = step.icon;

          return (
            <div
              key={index}
              className="rounded-2xl border bg-white p-8 shadow-sm transition hover:-translate-y-2 hover:shadow-xl"
            >
              <div className="flex h-16 w-16 items-center justify-center rounded-full bg-blue-100">
                <Icon className="h-8 w-8 text-blue-600" />
              </div>

              <h3 className="mt-6 text-2xl font-semibold">
                {step.title}
              </h3>

              <p className="mt-4 text-slate-600">
                {step.description}
              </p>
            </div>
          );
        })}
      </div>
    </section>
  );
}
