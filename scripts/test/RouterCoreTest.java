package com.castilhoduarte.jlh6;

public class RouterCoreTest {
    static int passed = 0, failed = 0;
    static void check(String n, boolean c) {
        if (c) { passed++; System.out.println("  ok   " + n); }
        else { failed++; System.out.println("  FAIL " + n); }
    }

    static final class Rig {
        final FakeClock clock = new FakeClock();
        final VirtualScheduler sched = new VirtualScheduler(clock);
        final KernelShell kernel = new KernelShell();
        final FakeStore store = new FakeStore();
        final RouterCore core = new RouterCore(clock, sched, kernel, store);
    }

    public static void main(String[] a) {
        scenarioBasicActivation();
        // later tasks append more scenario calls here
        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }

    // #1
    static void scenarioBasicActivation() {
        Rig r = new Rig();
        r.kernel.setUplinkUp(true);
        r.core.enable();
        r.sched.advance(0);
        check("#1 activation: ACTIVE", r.core.getState() == RouterCore.State.ACTIVE);
        check("#1 activation: fully applied (INV1)", r.kernel.fullyApplied());
        check("#1 activation: store enabled", r.store.isEnabled());
    }
}
