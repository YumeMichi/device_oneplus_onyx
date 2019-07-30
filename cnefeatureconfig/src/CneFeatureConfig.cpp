/*==============================================================================
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

==============================================================================*/

/*------------------------------------------------------------------------------
* Include Files
* ---------------------------------------------------------------------------*/
#include <cutils/properties.h>
#include<cstring>
#include "CneFeatureConfig.h"
#include <stdlib.h>

/*------------------------------------------------------------------------------
* Preprocessor Definitions and Constants
* ---------------------------------------------------------------------------*/
#undef LOG_TAG
#define LOG_TAG "CNEFEATURECONFIG"

/*-----------------------------------------------------------------------------
 * Global functions
 * --------------------------------------------------------------------------*/
bool isFeatureEnabled(int f)
{
    bool bEnabled = false;
    CneFeatureConfig *cneFeature = new CneFeatureConfig;

    if (NULL != cneFeature) {
        bEnabled = cneFeature->isEnabled((Feature) f);
        delete cneFeature;
    } else {
        CFC_LOGE("Failed to instantiate CneFeatureConfig! Default = disabled");
    }

    return bEnabled;
}

/*-----------------------------------------------------------------------------
 * Class methods
 * --------------------------------------------------------------------------*/
CneFeatureConfig::CneFeatureConfig():bCne(false),
    bFmc(false),
    bWqe(false),
    bNsrm(false),
    bAtp(false)
{
}

CneFeatureConfig::~CneFeatureConfig()
{
}

/* Read the currently set feature property value.
*/
void CneFeatureConfig::readFeature(void) {
    FeatureProperty val;
    char prop_value_feature[PROPERTY_VALUE_MAX] = {'\0'};
    property_get(CNE_FEATURE_PROP, prop_value_feature, "0");
    val = (FeatureProperty) atoi(prop_value_feature);

    switch (val) {
        case LINK_MGR: // 0 --> Use Link Manager. Cne is completely disabled.
        {
            // do nothing
            break;
        }
        case CNE_ONLY: // 1--> Only Cne default behavior enabled.
        {
             bCne = true;
             break;
        }
        case FMC_CNE: // 2--> CNE enabled. FMC mode
        {
            bCne = true;
            bFmc = true;
            break;
        }
        case NSRM_CNE: // 3--> CNE enabled. NSRM mode.
        {
            bCne = true;
            bNsrm = true;
            break;
        }
        case WQE_CNE: // 4--> CNE enabled. WQE mode.
        {
            bCne = true;
            bWqe = true;
            break;
        }
        case FMC_NSRM_CNE: //5 --> CNE enabled. FMC & NSRM mode.
        {
            bCne = true;
            bFmc = true;
            bNsrm = true;
            break;
        }
        case WQE_NSRM_CNE: //6 --> CNE enabled. WQE & NSRM mode.
        {
            bCne = true;
            bWqe = true;
            bNsrm = true;
            break;
        }
        case ATP_CNE: //7 --> CNE enabled. ATP mode.
        {
            bCne = true;
            bAtp = true;
            break;
        }
        case ATP_NSRM_CNE: //8 --> CNE enabled. ATP & NSRM mode.
        {
            bCne = true;
            bAtp = true;
            bNsrm = true;
            break;
        }
        case ATP_NSRM_WQE_CNE: //9 --> CNE enabled. ATP WQE & NSRM mode.
        {
            bCne = true;
            bAtp = true;
            bNsrm = true;
            bWqe = true;
            break;
        }
        default:
            CFC_LOGW("Unknown feature value in property. Features disabled by default");
    }
}

bool CneFeatureConfig::isEnabled(Feature f) {
    readFeature();
    switch(f){
        case CNE:
            return bCne;
            break;
        case FMC:
            return bFmc;
            break;
        case NSRM:
            return bNsrm;
            break;
        case WQE:
            return bWqe;
            break;
        case ATP:
            return bAtp;
            break;
        default:
            CFC_LOGW("Feature %d not known, returning default", f);
            return false;
    }

    return false;
}

