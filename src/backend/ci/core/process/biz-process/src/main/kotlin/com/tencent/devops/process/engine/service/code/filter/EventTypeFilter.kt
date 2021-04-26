/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CI 蓝鲸持续集成平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tencent.devops.process.engine.service.code.filter

import com.tencent.devops.common.pipeline.pojo.element.trigger.enums.CodeEventType
import org.slf4j.LoggerFactory

class EventTypeFilter(
    private val pipelineId: String,
    private val triggerOnEventType: CodeEventType,
    private val eventType: CodeEventType?,
    private val action: String? = null
) : WebhookFilter {

    companion object {
        private val logger = LoggerFactory.getLogger(EventTypeFilter::class.java)
    }

    override fun doFilter(response: WebhookFilterResponse): Boolean {
        return isAllowedByEventType() && isAllowedByMrAction()
    }

    private fun isAllowedByEventType(): Boolean {
        return eventType == triggerOnEventType
    }

    private fun isAllowedByMrAction(): Boolean {
        if (isMrAndMergeAction() || isMrAcceptNotMergeAction()) {
            logger.warn("$pipelineId|Git mr web hook not match with action($action)")
            return false
        }
        return true
    }

    private fun isMrAndMergeAction(): Boolean {
        return triggerOnEventType == CodeEventType.MERGE_REQUEST && action == "merge"
    }

    private fun isMrAcceptNotMergeAction(): Boolean {
        return triggerOnEventType == CodeEventType.MERGE_REQUEST_ACCEPT && action != "merge"
    }
}
