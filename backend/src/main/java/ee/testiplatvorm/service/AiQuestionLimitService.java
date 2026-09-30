package ee.testiplatvorm.service;

import ee.testiplatvorm.infrastructure.exception.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static ee.testiplatvorm.Error.AI_QUESTION_LIMIT_REACHED;

/**
 * Piirab, mitu küsimust saab üks kasutaja tunnis AI abil genereerida.
 * Andmeid hoitakse mälus, seega backendi taaskäivitamisel loendurid nullitakse.
 */
@Service
public class AiQuestionLimitService {

    public static final int MAX_QUESTIONS_PER_HOUR = 30;
    private static final Duration LIMIT_WINDOW = Duration.ofHours(1);

    private final Map<Integer, Deque<Instant>> generatedQuestionTimesByUserId = new HashMap<>();

    public synchronized int getRemainingQuestionCount(Integer userId) {
        Deque<Instant> generatedQuestionTimes = getRecentGeneratedQuestionTimes(userId);
        return MAX_QUESTIONS_PER_HOUR - generatedQuestionTimes.size();
    }

    public synchronized void validateLimitNotReached(Integer userId) {
        int remainingQuestionCount = getRemainingQuestionCount(userId);
        if (remainingQuestionCount <= 0) {
            throw createLimitReachedException(remainingQuestionCount);
        }
    }

    public synchronized void handleReserveQuestions(Integer userId, int questionCount) {
        Deque<Instant> generatedQuestionTimes = getRecentGeneratedQuestionTimes(userId);
        int remainingQuestionCount = MAX_QUESTIONS_PER_HOUR - generatedQuestionTimes.size();
        if (questionCount > remainingQuestionCount) {
            throw createLimitReachedException(remainingQuestionCount);
        }

        Instant now = Instant.now();
        for (int i = 0; i < questionCount; i++) {
            generatedQuestionTimes.addLast(now);
        }
    }

    // Kui AI tegi vähem korrektseid küsimusi kui broneeriti, antakse kasutamata kogus tagasi
    public synchronized void releaseQuestions(Integer userId, int questionCount) {
        Deque<Instant> generatedQuestionTimes = getRecentGeneratedQuestionTimes(userId);
        for (int i = 0; i < questionCount && !generatedQuestionTimes.isEmpty(); i++) {
            generatedQuestionTimes.removeLast();
        }
    }

    private TooManyRequestsException createLimitReachedException(int remainingQuestionCount) {
        String message = AI_QUESTION_LIMIT_REACHED.getMessage().formatted(MAX_QUESTIONS_PER_HOUR, Math.max(remainingQuestionCount, 0));
        return new TooManyRequestsException(message, AI_QUESTION_LIMIT_REACHED.name());
    }

    private Deque<Instant> getRecentGeneratedQuestionTimes(Integer userId) {
        Deque<Instant> generatedQuestionTimes = generatedQuestionTimesByUserId.computeIfAbsent(userId, id -> new ArrayDeque<>());
        Instant windowStart = Instant.now().minus(LIMIT_WINDOW);
        while (!generatedQuestionTimes.isEmpty() && generatedQuestionTimes.peekFirst().isBefore(windowStart)) {
            generatedQuestionTimes.removeFirst();
        }
        return generatedQuestionTimes;
    }
}
