package com.slicky.gitcompare.model

/**
 * Created by SlickyPC on 28.5.2017
 */
class GithubException(lazy: () -> String) : Exception(lazy.invoke())
