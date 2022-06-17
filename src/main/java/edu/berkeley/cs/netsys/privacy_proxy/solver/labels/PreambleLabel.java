package edu.berkeley.cs.netsys.privacy_proxy.solver.labels;

// Context-agnostic.
public interface PreambleLabel {
    enum Kind {
        DEPENDENCY,
        POLICY
    }

    Kind getKind();
}
