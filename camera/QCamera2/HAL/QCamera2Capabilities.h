/* Copyright (c) 2016, Sultanxda <sultanxda@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of The Linux Foundation nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

#define ARRAY_SIZE(a) (int)(sizeof(a)/sizeof((a)[0]))

static const cam_dimension_t new_pic_sizes_cam0[] = {
    {4208, 3120},
    {4160, 3120},
    {4160, 2340},
    {4000, 3000},
    {4096, 2160},
    {3200, 2400},
    {3200, 1800},
    {2592, 1944},
    {2048, 1536},
    {1920, 1080},
    {1600, 1200},
    {1280, 960},
    {1280, 768},
    {1280, 720},
    {1024, 768},
    {800, 600},
    {800, 480},
    {720, 480},
    {640, 480},
    {352, 288},
    {320, 240}
};

static const cam_dimension_t new_vid_sizes_cam0[] = {
    {4096, 2160},
    {3840, 2160},
    {2560, 1440},
    {1920, 1080},
    {1280, 960},
    {1280, 720},
    {800, 480},
    {720, 480},
    {640, 480},
    {480, 320},
    {352, 288},
    {320, 240},
    {176, 144},
    {160, 120}
};

static const cam_dimension_t new_prvw_sizes_cam0[] = {
    {4096, 2160},
    {3840, 2160},
    {2560, 1440},
    {1920, 1080},
    {1440, 1080},
    {1280, 960},
    {1280, 720},
    {864, 480},
    {800, 480},
    {768, 432},
    {720, 480},
    {640, 480},
    {576, 432},
    {480, 320},
    {384, 288},
    {320, 240}
};

static const cam_dimension_t new_pic_sizes_cam1[] = {
    {3264, 2448},
    {2592, 1944},
    {2048, 1536},
    {1920, 1080},
    {1600, 1200},
    {1280, 960},
    {1280, 768},
    {1280, 720},
    {1024, 768},
    {800, 600},
    {800, 480},
    {720, 480},
    {640, 480},
    {352, 288},
    {320, 240}
};

static const cam_dimension_t new_vid_sizes_cam1[] = {
    {2560, 1440},
    {1920, 1080},
    {1280, 720},
    {864, 480},
    {800, 480},
    {720, 480},
    {640, 480},
    {480, 320},
    {352, 288},
    {320, 240},
    {176, 144}
};

static const cam_dimension_t new_prvw_sizes_cam1[] = {
    {2560, 1440},
    {1920, 1080},
    {1440, 1080},
    {1280, 960},
    {1280, 720},
    {864, 480},
    {800, 480},
    {768, 432},
    {720, 480},
    {640, 480},
    {576, 432},
    {480, 320},
    {320, 240}
};

static const cam_format_t new_prvw_fmts[] = {
    CAM_FORMAT_YUV_420_NV21,
    CAM_FORMAT_YUV_420_YV12,
    CAM_FORMAT_YUV_420_NV12_VENUS
};

static const cam_fps_range_t new_fps_ranges_cam0[] = {
    {7.5, 30.06, 0.0, 0.0},
    {7.5, 60.0, 0.0, 0.0},
    {7.5, 120.0, 0.0, 0.0}
};

static const cam_fps_range_t new_fps_ranges_cam1[] = {
    {7.5, 30.0, 0.0, 0.0},
    {15.0, 30.0, 0.0, 0.0}
};

static const cam_focus_mode_type new_focus_modes_cam0[] = {
    CAM_FOCUS_MODE_AUTO,
    CAM_FOCUS_MODE_INFINITY,
    CAM_FOCUS_MODE_MACRO,
    CAM_FOCUS_MODE_CONTINOUS_VIDEO,
    CAM_FOCUS_MODE_CONTINOUS_PICTURE,
    CAM_FOCUS_MODE_MANUAL
};

static const cam_auto_exposure_mode_type new_aec_modes[] = {
    CAM_AEC_MODE_FRAME_AVERAGE,
    CAM_AEC_MODE_CENTER_WEIGHTED,
    CAM_AEC_MODE_SPOT_METERING,
    CAM_AEC_MODE_SPOT_METERING_ADV,
    CAM_AEC_MODE_CENTER_WEIGHTED_ADV
};

static const cam_antibanding_mode_type new_antibanding_modes[] = {
    CAM_ANTIBANDING_MODE_60HZ,
    CAM_ANTIBANDING_MODE_50HZ,
    CAM_ANTIBANDING_MODE_AUTO
};

static const cam_effect_mode_type new_effect_modes[] = {
    CAM_EFFECT_MODE_OFF,
    CAM_EFFECT_MODE_NEGATIVE,
    CAM_EFFECT_MODE_SOLARIZE,
    CAM_EFFECT_MODE_WHITEBOARD,
    CAM_EFFECT_MODE_BLACKBOARD,
    CAM_EFFECT_MODE_EMBOSS,
    CAM_EFFECT_MODE_SKETCH,
    CAM_EFFECT_MODE_NEON
};

static const cam_wb_mode_type new_wb_modes[] = {
    CAM_WB_MODE_AUTO,
    CAM_WB_MODE_INCANDESCENT,
    CAM_WB_MODE_FLUORESCENT,
    CAM_WB_MODE_WARM_FLUORESCENT,
    CAM_WB_MODE_DAYLIGHT,
    CAM_WB_MODE_CLOUDY_DAYLIGHT,
    CAM_WB_MODE_TWILIGHT,
    CAM_WB_MODE_SHADE,
    CAM_WB_MODE_CCT
};

static const cam_flash_mode_t new_flash_modes_cam0[] = {
    CAM_FLASH_MODE_OFF,
    CAM_FLASH_MODE_AUTO,
    CAM_FLASH_MODE_ON,
    CAM_FLASH_MODE_TORCH
};

static const cam_scene_mode_type new_scene_modes[] = {
    CAM_SCENE_MODE_OFF,
    CAM_SCENE_MODE_AUTO,
    CAM_SCENE_MODE_LANDSCAPE,
    CAM_SCENE_MODE_SNOW,
    CAM_SCENE_MODE_BEACH,
    CAM_SCENE_MODE_SUNSET,
    CAM_SCENE_MODE_NIGHT,
    CAM_SCENE_MODE_PORTRAIT,
    CAM_SCENE_MODE_BACKLIGHT,
    CAM_SCENE_MODE_SPORTS,
    CAM_SCENE_MODE_ANTISHAKE,
    CAM_SCENE_MODE_FLOWERS,
    CAM_SCENE_MODE_CANDLELIGHT,
    CAM_SCENE_MODE_FIREWORKS,
    CAM_SCENE_MODE_PARTY,
    CAM_SCENE_MODE_NIGHT_PORTRAIT,
    CAM_SCENE_MODE_THEATRE,
    CAM_SCENE_MODE_ACTION,
    CAM_SCENE_MODE_AR
};

static const cam_iso_mode_type new_iso_modes[] = {
    CAM_ISO_MODE_AUTO,
    CAM_ISO_MODE_100,
    CAM_ISO_MODE_200,
    CAM_ISO_MODE_400,
    CAM_ISO_MODE_800,
    CAM_ISO_MODE_1600,
    CAM_ISO_MODE_3200
};

static const cam_focus_algorithm_type new_focus_algos[] = {
    CAM_FOCUS_ALGO_AUTO,
    CAM_FOCUS_ALGO_SPOT,
    CAM_FOCUS_ALGO_CENTER_WEIGHTED,
    CAM_FOCUS_ALGO_AVERAGE
};

static const int new_zoom_ratios[] = {
    100, 102, 104, 107, 109, 112,
    114, 117, 120, 123, 125, 128,
    131, 135, 138, 141, 144, 148,
    151, 155, 158, 162, 166, 170,
    174, 178, 182, 186, 190, 195,
    200, 204, 209, 214, 219, 224,
    229, 235, 240, 246, 251, 257,
    263, 270, 276, 282, 289, 296,
    303, 310, 317, 324, 332, 340,
    348, 356, 364, 373, 381, 390,
    400, 409, 418, 428, 438, 448,
    459, 470, 481, 492, 503, 515,
    527, 540, 552, 565, 578, 592,
    606
};
