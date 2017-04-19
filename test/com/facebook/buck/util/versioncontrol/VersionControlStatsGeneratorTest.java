/*
 * Copyright 2017-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.util.versioncontrol;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.google.common.collect.ImmutableSet;

import org.junit.Test;

import java.util.Optional;

public class VersionControlStatsGeneratorTest {

  private final VersionControlStats expected = VersionControlStats.builder()
      .setCurrentRevisionId("f00")
      .setBranchedFromMasterRevisionId("b47")
      .setBranchedFromMasterTS(0L)
      .setBaseBookmarks(ImmutableSet.of("remote/master"))
      .setDiff("this is not really a valid diff but whatever")
      .setPathsChangedInWorkingDirectory(ImmutableSet.of("hello.txt"))
      .build();

  private final VersionControlCmdLineInterface versionControlCmdLineInterface =
      new FakeVersionControlCmdLineInterface(expected);

  @Test
  public void fastModeGeneratesBasicStats() throws Exception {
    Optional<VersionControlStats> actual =
        new VersionControlStatsGenerator(versionControlCmdLineInterface)
            .generateStats(VersionControlStatsGenerator.Mode.FAST);
    assertThat(actual.isPresent(), is(equalTo(true)));
    assertThat(actual.get().getCurrentRevisionId(), equalTo(expected.getCurrentRevisionId()));
    assertThat(
        actual.get().getBranchedFromMasterRevisionId(),
        is(equalTo(expected.getBranchedFromMasterRevisionId())));
    assertThat(
        actual.get().getBranchedFromMasterTS(),
        is(equalTo(expected.getBranchedFromMasterTS())));
    assertThat(
        actual.get().getBaseBookmarks(),
        is(equalTo(expected.getBaseBookmarks())));
  }

  @Test
  public void fastModeDoesNotGenerateChangedFilesAndDiff() throws Exception {
    Optional<VersionControlStats> actual =
        new VersionControlStatsGenerator(versionControlCmdLineInterface)
            .generateStats(VersionControlStatsGenerator.Mode.FAST);
    assertThat(actual.isPresent(), is(equalTo(true)));
    assertThat(actual.get().getPathsChangedInWorkingDirectory(), empty());
    assertThat(actual.get().getDiff().isPresent(), is(equalTo(false)));
  }

  @Test
  public void fullModeGeneratesChangedFilesAndDiff() throws Exception {
    Optional<VersionControlStats> actual =
        new VersionControlStatsGenerator(versionControlCmdLineInterface)
            .generateStats(VersionControlStatsGenerator.Mode.FULL);
    assertThat(actual.isPresent(), is(equalTo(true)));
    assertThat(
        actual.get().getPathsChangedInWorkingDirectory(),
        is(equalTo(expected.getPathsChangedInWorkingDirectory())));
    assertThat(actual.get().getDiff(), is(equalTo(expected.getDiff())));
  }

}
