/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/spanner/v1/spanner.proto

package com.google.spanner.v1;

public interface ListSessionsRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.spanner.v1.ListSessionsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. The database in which to list sessions.
   * </pre>
   *
   * <code>
   * string database = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The database.
   */
  java.lang.String getDatabase();
  /**
   *
   *
   * <pre>
   * Required. The database in which to list sessions.
   * </pre>
   *
   * <code>
   * string database = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for database.
   */
  com.google.protobuf.ByteString getDatabaseBytes();

  /**
   *
   *
   * <pre>
   * Number of sessions to be returned in the response. If 0 or less, defaults
   * to the server's maximum allowed page size.
   * </pre>
   *
   * <code>int32 page_size = 2;</code>
   *
   * @return The pageSize.
   */
  int getPageSize();

  /**
   *
   *
   * <pre>
   * If non-empty, `page_token` should contain a
   * [next_page_token][google.spanner.v1.ListSessionsResponse.next_page_token]
   * from a previous
   * [ListSessionsResponse][google.spanner.v1.ListSessionsResponse].
   * </pre>
   *
   * <code>string page_token = 3;</code>
   *
   * @return The pageToken.
   */
  java.lang.String getPageToken();
  /**
   *
   *
   * <pre>
   * If non-empty, `page_token` should contain a
   * [next_page_token][google.spanner.v1.ListSessionsResponse.next_page_token]
   * from a previous
   * [ListSessionsResponse][google.spanner.v1.ListSessionsResponse].
   * </pre>
   *
   * <code>string page_token = 3;</code>
   *
   * @return The bytes for pageToken.
   */
  com.google.protobuf.ByteString getPageTokenBytes();

  /**
   *
   *
   * <pre>
   * An expression for filtering the results of the request. Filter rules are
   * case insensitive. The fields eligible for filtering are:
   *
   *   * `labels.key` where key is the name of a label
   *
   * Some examples of using filters are:
   *
   *   * `labels.env:*` --&gt; The session has the label "env".
   *   * `labels.env:dev` --&gt; The session has the label "env" and the value of
   *                        the label contains the string "dev".
   * </pre>
   *
   * <code>string filter = 4;</code>
   *
   * @return The filter.
   */
  java.lang.String getFilter();
  /**
   *
   *
   * <pre>
   * An expression for filtering the results of the request. Filter rules are
   * case insensitive. The fields eligible for filtering are:
   *
   *   * `labels.key` where key is the name of a label
   *
   * Some examples of using filters are:
   *
   *   * `labels.env:*` --&gt; The session has the label "env".
   *   * `labels.env:dev` --&gt; The session has the label "env" and the value of
   *                        the label contains the string "dev".
   * </pre>
   *
   * <code>string filter = 4;</code>
   *
   * @return The bytes for filter.
   */
  com.google.protobuf.ByteString getFilterBytes();
}
