#!/bin/bash
#
# Copyright (C) 2017 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

filename="proprietary-files.txt"
filelines="$(cat $filename)"
vendor="../../../vendor/oneplus/onyx/proprietary"

for line in $filelines ; do
    if [[ $line == *"|"* ]]; then

        # Get the name of the blob and remove '-'
        # when it is the first character
        blob=$(echo $line | cut -f1 -d'|' | sed 's/^-//g')

        # Get the old sha1sum
        oldSHA1=$(echo $line | cut -f2 -d'|')

        # Get the new sha1sum from vendor path
        newSHA1=$(sha1sum $vendor/$blob | awk '{print $1}')

        # Replace oldSHA1 with newSHA1
        sed -i "s/$oldSHA1/$newSHA1/g" "$filename"

    fi
done
