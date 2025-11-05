export const QK = {
  me: ['me'] as const,
  users: ['users'] as const,
  user: (id: string) => ['user', id] as const,
  userLanguages: (id: string) => ['user', id, 'languages'] as const,
  projects: ['projects'] as const,
  project: (id: string) => ['project', id] as const,
  projectFeedback: (id: string) => ['project', id, 'feedback'] as const,
};
