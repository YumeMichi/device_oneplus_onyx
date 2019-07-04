/*
 * Copyright (C) 2019 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "TouchscreenGestureService"

#include <android-base/file.h>
#include <android-base/logging.h>

#include "TouchscreenGesture.h"

namespace vendor {
namespace mokee {
namespace touch {
namespace V1_0 {
namespace implementation {

const std::map<int32_t, TouchscreenGesture::GestureInfo> TouchscreenGesture::kGestureInfoMap = {
    {0, {252, "Up arrow", "/proc/touchpanel/up_arrow_enable"}},
    {1, {253, "Down arrow", "/proc/touchpanel/down_arrow_enable"}},
    {2, {254, "Left arrow", "/proc/touchpanel/left_arrow_enable"}},
    {3, {255, "Right arrow", "/proc/touchpanel/right_arrow_enable"}},
    {4, {251, "Two finger down swipe", "/proc/touchpanel/double_swipe_enable"}},
    {5, {250, "Letter O", "/proc/touchpanel/letter_o_enable"}},
    {6, {257, "Letter M", "/proc/touchpanel/letter_m_enable"}},
    {7, {256, "Letter W", "/proc/touchpanel/letter_w_enable"}},
    {8, {258, "One finger right swipe", "/proc/touchpanel/ltr_swipe_enable"}},
    {9, {259, "One finger left swipe", "/proc/touchpanel/rtl_swipe_enable"}},
    {10, {260, "One finger down swipe", "/proc/touchpanel/utd_swipe_enable"}},
    {11, {261, "One finger up swipe", "/proc/touchpanel/dtu_swipe_enable"}},
};

// Methods from ::vendor::mokee::touch::V1_0::ITouchscreenGesture follow.
Return<void> TouchscreenGesture::getSupportedGestures(getSupportedGestures_cb resultCb) {
    std::vector<Gesture> gestures;

    for (const auto& entry : kGestureInfoMap) {
        gestures.push_back({entry.first, entry.second.name, entry.second.keycode});
    }
    resultCb(gestures);

    return Void();
}

Return<bool> TouchscreenGesture::setGestureEnabled(const Gesture& gesture, bool enabled) {
    const auto entry = kGestureInfoMap.find(gesture.id);
    if (entry == kGestureInfoMap.end()) {
        return false;
    }

    if (!android::base::WriteStringToFile((enabled ? "1" : "0"), entry->second.path)) {
        LOG(ERROR) << "Failed to write " << entry->second.path;
        return false;
    }

    return true;
}

}  // namespace implementation
}  // namespace V1_0
}  // namespace touch
}  // namespace mokee
}  // namespace vendor
