// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

#pragma once

#include <string>

#include "common/utils.h"
#include "http/http_request.h"

struct bufferevent_rate_limit_group;

namespace doris {

struct AuthInfo;

std::string encode_basic_auth(const std::string& user, const std::string& passwd);
// parse Basic authorization
// return true, if request contain valid basic authorization.
// Otherwise return false
bool parse_basic_auth(const HttpRequest& req, std::string* user, std::string* passwd);

bool parse_basic_auth(const HttpRequest& req, AuthInfo* auth);

void do_file_response(const std::string& dir_path, HttpRequest* req,
                      bufferevent_rate_limit_group* rate_limit_group = nullptr,
                      bool is_acquire_md5 = false);

void do_dir_response(const std::string& dir_path, HttpRequest* req,
                     bool is_acquire_filesize = false);

std::string get_content_type(const std::string& file_name);

bool load_size_smaller_than_wal_limit(int64_t content_length);

// Whether a backend supports batch download
Status is_support_batch_download(const std::string& address);

Status list_remote_files_v2(const std::string& address, const std::string& token,
                            const std::string& remote_dir,
                            std::vector<std::pair<std::string, size_t>>* file_info_list);

Status download_files_v2(const std::string& address, const std::string& token,
                         const std::string& remote_dir, const std::string& local_dir,
                         const std::vector<std::pair<std::string, size_t>>& file_info_list);

} // namespace doris
