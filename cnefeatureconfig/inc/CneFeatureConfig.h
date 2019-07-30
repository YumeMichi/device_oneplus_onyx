#ifndef CneFeatureConfig_H
#define CneFeatureConfig_H

/*=========================================================================
 Copyright (c) 2012, 2014 The Linux Foundation. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of The Linux Foundation nor the names of its
      contributors may be used to endorse or promote products derived
      from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
=========================================================================*/

/* Enum Feature represents values to be used to
*  query whether a feature is enabled.
*/

enum Feature {
    CNE = 1,
    FMC = 2,
    WQE = 3,
    NSRM = 4,
    ATP = 5,
};

#include <cutils/log.h>

#ifdef LOGE
#define CFC_LOGE LOGE
#else
#define CFC_LOGE ALOGE
#endif

#ifdef LOGW
#define CFC_LOGW LOGW
#else
#define CFC_LOGW ALOGW
#endif

#ifdef LOGD
#define CFC_LOGD LOGD
#else
#define CFC_LOGD ALOGD
#endif

#ifdef LOGI
#define CFC_LOGI LOGI
#else
#define CFC_LOGI ALOGI
#endif

#ifdef LOGV
#define CFC_LOGV LOGV
#else
#define CFC_LOGV ALOGV
#endif

#define CNE_FEATURE_PROP "persist.cne.feature"

#ifdef __cplusplus
extern "C"
#endif /*__cplusplus*/
/* Provide C function to be used by JNI and other C/C++ modules.
 *  Parameter should be one of the following values:
 *  CNE
 *  FMC,
 *  WQE,
 *  NSRM
*/
bool isFeatureEnabled(int f);

#ifdef __cplusplus
/* Allow clients to query what features of CNE are
 * enabled.
 */
class CneFeatureConfig {
private:
    bool bCne;
    bool bFmc;
    bool bWqe;
    bool bNsrm;
    bool bAtp;
    /* Reads the CNE_FEATURE_PROP to determine which features
       are currently enabled.*/
    void readFeature(void);

    /* Enum FeatureProperty values represents all permissible feature values.
     * CNE_FEATURE_PROP must have one of these values:
     * CNE_ONLY --> bCne set true
     * FMC_CNE --> bCne & bFmc set true
     * WQE_CNE --> bFmc & bCne set true
     * NSRM_CNE --> bNsrm & bCne set true
     * FMC_NSRM_CNE --> bFmc, bNsrm, bCne set true
     * WQE_NSRM_CNE --> bWqe, bNsrm, bCne set true
     * Any other value will return false. By default
     * LinkManager used if CNE disabled.
     */
    enum FeatureProperty {
        LINK_MGR = 0, /* Link Manager - no CNE features enabled */
        CNE_ONLY = 1, /* CNE only */
        FMC_CNE = 2, /* CNE feature is prerequisite */
        WQE_CNE = 3, /* CNE feature is prerequisite */
        NSRM_CNE = 4, /* CNE feature is prerequisite */
        FMC_NSRM_CNE = 5, /* CNE feature is prerequisite */
        WQE_NSRM_CNE = 6, /* CNE feature is prerequisite */
        ATP_CNE = 7, /* CNE feature is prerequisite */
        ATP_NSRM_CNE = 8, /* CNE feature is prerequisite */
        ATP_NSRM_WQE_CNE = 9, /* CNE feature is prerequisite */
    };

public:
    bool isEnabled(Feature f);
    CneFeatureConfig();
    ~CneFeatureConfig();
};

#endif /*__cplusplus*/

#endif /*CneFeatureConfig_H*/

