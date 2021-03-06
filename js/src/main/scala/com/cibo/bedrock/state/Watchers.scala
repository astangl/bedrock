/*
 * Copyright (c) 2018, CiBO Technologies, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cibo.bedrock.state

import java.util.UUID
import japgolly.scalajs.react.{Callback, CallbackTo, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^.{<, TagMod, VdomElement}

import scala.collection.mutable

// only supports one subscriber
class SingleWatcher[A](initialState: A) {
  private var currentState: A = initialState
  private var stateUpdatedListener: Option[A => Unit] = None

  def updateState(state: A): Unit = {
    currentState = state
    stateUpdatedListener.foreach(_(state))
  }

  def getCurrentState : A = currentState

  def subscribe(event: A => Unit): Unit = stateUpdatedListener = Some(event)

  def unsubscribe(): Unit = stateUpdatedListener = None
}

class MultiWatcher[A](initialState: A) {

  private var currentState: A = initialState

  private val stateUpdatedListener: mutable.Map[String, A => Unit] = mutable.Map.empty[String, A => Unit]

  def modState(stateMod: A => A): Unit = {
    currentState = stateMod(currentState)
    notifySubscribers()
  }

  def setState(state: A): Unit = {
    currentState = state
    notifySubscribers()
  }

  private def notifySubscribers(): Unit = {

    stateUpdatedListener.foreach(_._2(currentState))
  }

  def getCurrentState : A = currentState

  def subscribe(event: A => Unit): String = {
    val id = UUID.randomUUID().toString
    stateUpdatedListener.+=(id -> event)
    id
  }

  def unsafeUnsubscribeAll(): Unit = stateUpdatedListener.clear()

  def unsubscribe(id: String): Unit = {
    stateUpdatedListener.-=(id)
  }
}
