package com.jetbrains.pluginverifier;

import com.intellij.structure.domain.Ide;
import com.intellij.structure.domain.Jdk;
import com.intellij.structure.resolvers.Resolver;
import com.jetbrains.pluginverifier.problems.Problem;
import com.jetbrains.pluginverifier.results.ProblemLocation;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergey Evdokimov
 */
public interface VerificationContext {
  PluginVerifierOptions getVerifierOptions();

  void registerProblem(@NotNull Problem problem, @NotNull ProblemLocation location);

  Ide getIde();

  Jdk getJdk();

  Resolver getExternalClassPath();
}
